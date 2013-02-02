package com.under_hair.topicwords

import org.apache.hadoop.fs.{Path, FileUtil}
import org.apache.mahout.common.iterator.sequencefile.SequenceFileIterable
import org.apache.hadoop.io.Writable
import org.apache.hadoop.conf.Configuration
import java.io.{File, IOException}
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
            println(cluster.clusterId + "," + article.articleId + " " + d)
            if (min > d) {
              min = d
              minId = cluster.clusterId
            }
          }}
          if (minId != null) {
            println(minId + " <- " + article.articleId)
            clusters(minId).add(article)
          }
          break
        }
        case _ => println("token not found")
      }
    })
    }

    //文書話題度を算出
    //ArticleClusterごとに単語話題度を算出
    /*
     */

    println("done")
  }
}
