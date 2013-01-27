package com.under_hair.topicwords

/**
 * Created with IntelliJ IDEA.
 * User: mastakeu
 * Date: 2013/01/27
 * Time: 17:55
 * To change this template use File | Settings | File Templates.
 */
case class Article(articleId: String, createTime: Long, words: List[String]) {
  val T = 60 * 60 * 12
  def freshness(now: Long) = math.exp((createTime - now).toDouble / T.toDouble)
}
