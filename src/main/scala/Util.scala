package com.under_hair.topicwords

import org.apache.mahout.common.distance.CosineDistanceMeasure

/**
 * Created with IntelliJ IDEA.
 * User: mastakeu
 * Date: 2013/01/27
 * Time: 17:56
 * To change this template use File | Settings | File Templates.
 */
object Util {
  def averageSimilarityMap(cluster: ArticleCluster) = {
    val measure = new CosineDistanceMeasure
    val articleCount = cluster.articles.size
    var averageSimilarityMap = Map[String,Double]()

    var similarityMap = Map[String,Double]()
    //クラスタ内の全ての記事との平均距離を計算する
    cluster.articles.foreach { case (id1,article1) => {
      var sum:Double = 0
      cluster.articles.foreach { case (id2,article2) => {
        val ids = List(id1, id2).sorted
        val key = ids.mkString
        similarityMap.get(key) match {
          case Some(score) => {
            //既知の類似度
            sum = sum + score
          }
          case None => {
            val d = measure.distance(article1.vector, article2.vector)
            sum = sum + d
            similarityMap += (key -> d)
          }
        }
      }}
      println(id1 + ":" + sum)
      averageSimilarityMap += (id1 -> sum / articleCount.toDouble)
    }}
    averageSimilarityMap
  }
}
