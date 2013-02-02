package com.under_hair.topicwords

import org.specs2.mutable.Specification
import org.joda.time.DateTime

/**
 * Created with IntelliJ IDEA.
 * User: mastakeu
 * Date: 2013/01/30
 * Time: 22:34
 * To change this template use File | Settings | File Templates.
 */
class WordClusterSpec extends Specification {
  val dt = new DateTime
  val articleCluster1 = new ArticleCluster("C-01")
  val article11 = new Article(
    "article11",
    dt.minusDays(1).getMillis / 1000,
    List("test1", "test2")
  )
  val article12 = new Article(
    "article12",
    dt.minusDays(2).getMillis / 1000,
    List("test2", "test3", "test4")
  )
  articleCluster1.add(article11)
  articleCluster1.add(article12)

  val articleCluster2 = new ArticleCluster("C-02")
  val article21 = new Article(
    "article21",
    dt.minusDays(1).getMillis / 1000,
    List("test2", "test4", "test5")
  )
  val article22 = new Article(
    "article22",
    dt.minusDays(1).getMillis / 1000,
    List("test2", "test4", "test6")
  )
  articleCluster2.add(article21)
  articleCluster2.add(article22)

  val wordCluster = new WordCluster(List(articleCluster1, articleCluster2))
  "WordCluster" should {
    "wordToClusterCount test" in {
      wordCluster.wordToClusterCount("test1").get must beEqualTo(1)
      wordCluster.wordToClusterCount("test2").get must beEqualTo(2)
      wordCluster.wordToClusterCount("test3").get must beEqualTo(1)
      wordCluster.wordToClusterCount("test4").get must beEqualTo(2)
      wordCluster.wordToClusterCount("test5").get must beEqualTo(1)
      wordCluster.wordToClusterCount("test6").get must beEqualTo(1)
    }
    "wordToArticles test" in {
      wordCluster.wordToArticles("C-01", "test1").get.head must beEqualTo(article11)
      val list = wordCluster.wordToArticles("C-01", "test2").get
      list.head must beEqualTo(article12)
      list.tail.head must beEqualTo(article11)
    }
  }
}
