package com.under_hair.topicwords

/**
 * Created with IntelliJ IDEA.
 * User: mastakeu
 * Date: 2013/01/27
 * Time: 17:55
 * To change this template use File | Settings | File Templates.
 */
case class WordCluster(clusters: List[ArticleCluster]) {
  private def _makeMap = {
    var clusterCount = Map[String,Int]()
    var clusterMap = Map[String,ArticleCluster]()
    clusters.foreach{ cluster => {
      cluster.words.keys.foreach( word => {
        clusterCount.get(word) match {
          case Some(count) => clusterCount += (word -> (count + 1))
          case None => clusterCount += (word -> 1)
        }
      })
      clusterMap += (cluster.clusterId -> cluster)
    }}
    (clusterMap, clusterCount)
  }
  private val _maps = _makeMap
  private val _clusterMap = _maps._1
  private val _wordToClusterCount = _maps._2

  def wordToClusterCount(w: String) = _wordToClusterCount.get(w)
  def wordToArticles(clusterId: String, w: String) = {
    _clusterMap.get(clusterId) match {
      case Some(cluster) => cluster.words.get(w)
      case _ => None
    }
  }

}
