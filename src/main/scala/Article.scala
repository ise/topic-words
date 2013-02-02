package com.under_hair.topicwords

import org.apache.mahout.math.Vector

/**
 * Created with IntelliJ IDEA.
 * User: mastakeu
 * Date: 2013/01/27
 * Time: 17:55
 * To change this template use File | Settings | File Templates.
 */
case class Article(articleId: String, createTime: Long, words: List[String]) {
  val T = 60 * 60 * 12
  var vector:Vector = null
  def this(articleId: String, createTime: Long, words: List[String], vec: Vector) = {
    this(articleId, createTime, words)
    vector = vec
  }
  def freshness(now: Long) = math.exp((createTime - now).toDouble / T.toDouble)
}
