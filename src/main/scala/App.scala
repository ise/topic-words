package com.under_hair.topicwords

import org.apache.hadoop.fs.Path
import org.apache.mahout.common.iterator.sequencefile.SequenceFileIterable
import org.apache.hadoop.io.Writable
import org.apache.hadoop.conf.Configuration
import scala.collection.JavaConversions._

import util.control.Breaks._
import org.apache.mahout.clustering.iterator.ClusterWritable
import org.apache.mahout.common.StringTuple
import org.joda.time.DateTime
import org.apache.mahout.math.VectorWritable
import org.apache.mahout.common.distance.CosineDistanceMeasure

object App {
  def main(args: Array[String]) {
    // (ファイル名 -> ワード一覧) なるMapを作成
    val conf = new Configuration

    def _tokenMap = {
      val conf = new Configuration
      var tokenMap = Map[String,List[String]]()

      //val dt = new DateTime
      val path = "tokenized.seq"
      val seq = new SequenceFileIterable[Writable, Writable](new Path(path), true, conf)
      seq.foreach( record => {
        tokenMap += (record.getFirst.toString -> record.getSecond.asInstanceOf[StringTuple].getEntries.toList)
      })
      tokenMap
    }

    //クラスタデータを読み込んでリストにして返す
    def _loadClusters = {
      val conf = new Configuration
      var clusters = Map[String,ArticleCluster]()
      val path = "cluster.seq"
      val seq = new SequenceFileIterable[Writable, Writable](new Path(path), true, conf)
      seq.foreach( record => {
        val ac = new ArticleCluster(record.getFirst.toString, record.getSecond.asInstanceOf[ClusterWritable].getValue.getCenter)
        clusters += (ac.clusterId -> ac)
      })
      clusters
    }

    val clusters = _loadClusters
    val tokenMap = _tokenMap

    //Articleを、属するArticleClusterに追加していく
    val dt = new DateTime
    val path = "vectors.seq"
    val seq = new SequenceFileIterable[Writable, Writable](new Path(path), true, conf)
    val measure = new CosineDistanceMeasure()
    breakable {
    seq.foreach( record => {
      val articleId = record.getFirst.toString
      tokenMap.get(articleId) match {
        case Some(list) => {
          //Articleインスタンスを作成し、
          //距離が最小となるArticleClusterに追加

          val article = new Article(
            articleId,
            dt.minusDays(1).getMillis / 1000,//適当
            list,
            record.getSecond.asInstanceOf[VectorWritable].get
          )

          var min = Double.MaxValue
          var minId:String = null
          clusters.foreach { case (id, cluster) => {
            val d = measure.distance(article.vector, cluster.vector)
            if (min > d) {
              min = d
              minId = cluster.clusterId
            }
          }}
          if (minId != null) {
            clusters(minId).add(article)
          }
        }
        case _ => println("token not found")
      }
    })
    }

    //文書話題度を算出
    //ArticleClusterごとに単語話題度を算出
    val now:Long = dt.getMillis / 1000
    val wordCluster = new WordCluster(clusters.values.toList)
    var aTopicScoreMap = Map[String,ArticleTopicScore]()//文書話題度
    var wTopicScoreMapMap = Map[String,Map[String,WordTopicScore]]()

    def _totalArticleTopicScore(cluster: ArticleCluster, word: String) = {
      var total:Double = 0
      wordCluster.wordToArticles(cluster.clusterId, word) match {
        case Some(list) => {
          list.foreach { a => {
            total = total + aTopicScoreMap(a.articleId).score
          }}
        }
        case None => println(word + " does not contain any articles")
      }
      total
    }

    clusters.foreach { case (id,cluster) => {
      /*if (cluster.articles.size > 3) {
      }*/
      //文書話題度の計算
      val asMap = Util.averageSimilarityMap(cluster)
      cluster.articles.foreach { case (id,article) => {
        val score = asMap(id) * article.freshness(now)
        //println(id + " -> " + score)
        aTopicScoreMap += (id -> new ArticleTopicScore(article, score))
      }}

      //単語話題度の計算
      var wTopicScoreMap = Map[String,WordTopicScore]()//単語話題度
      cluster.articles.foreach { case (id,article) => {
        article.words.foreach { word => {
          wTopicScoreMap.get(word) match {
            case Some(score) => println("Already calculated: " + word)
            case None => {
              //クラスタ内でwordを含む全記事について文書話題度を合計
              val total = _totalArticleTopicScore(cluster, word)
              //wordを含む記事の属すクラスタの数を取得
              wordCluster.wordToClusterCount(word) match {
                case Some(c) => {
                  wTopicScoreMap += (word -> new WordTopicScore(word, total * math.log(clusters.size.toDouble / c.toDouble)))
                }
                case None => println("ClusterCount not found")
              }
            }
          }
        }}
      }}

      wTopicScoreMapMap += (cluster.clusterId -> wTopicScoreMap)
    }}

    //標準出力
    clusters.foreach { case (id,cluster) => {
      println("Cluster: " + id + " => ")
      wTopicScoreMapMap.get(id) match {
        case Some(map) => {
          map.foreach { case (word, wts) => {
            println(word + "\t" + wts.score)
          }}
        }
        case None => println(id + " not found")
      }
    }}
    /*

    欲しいoutput =>
    ---------------------------------
    clusterId =>
    [topic word1]¥t[score1]
    [topic word2]¥t[score2]
    ...

    [topic article1]¥t[score1]
    [topic article2]¥t[score2]
    ...

    ---------------------------------
    clusterId =>
    [topic word1]¥t[score1]
    [topic word2]¥t[score2]
    ...

     */

    println("done")
  }
}
