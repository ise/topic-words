package com.under_hair.topicwords
/**
 * Created with IntelliJ IDEA.
 * User: mastakeu
 * Date: 2013/01/27
 * Time: 17:55
 * To change this template use File | Settings | File Templates.
 */
case class ArticleCluster(clusterId: String) {
  private var _articles = Map[String,Article]()
  private var _words = Map[String,List[Article]]()
  def add(a: Article):Boolean = {
    _articles.get(a.articleId) match {
      case Some(a) => return false
      case None => _articles += (a.articleId -> a)
    }
    a.words.foreach(word => {
      _words.get(word) match {
        case Some(list) => _words += (word -> (a :: list))
        case None => _words += (word -> List(a))
      }
    })
    return true
  }
  def articles = _articles
  def words = _words
}
