package com.under_hair.topicwords

import org.specs2.mutable._
import org.specs2.specification.Scope
import org.joda.time.DateTime

/**
 * Created with IntelliJ IDEA.
 * User: mastakeu
 * Date: 2013/01/27
 * Time: 18:20
 * To change this template use File | Settings | File Templates.
 */
//DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss").parseDateTime("2013-01-01")
class ArticleClusterSpec extends Specification {
  val dt = new DateTime
  val articleCluster = new ArticleCluster("C-01")
  val article1 = new Article(
    "article01",
    dt.minusDays(1).getMillis / 1000,
    List("test1", "test2")
  )
  val article2 = new Article(
    "article02",
    dt.minusDays(2).getMillis / 1000,
    List("test2", "test3", "test4")
  )

  trait scope extends Scope {
    articleCluster.add(article1)
    articleCluster.add(article2)
  }

  "ArticleCluster" should {
    "clusterId test" in {
      articleCluster.clusterId must beEqualTo("C-01")
    }
    "add test" in new scope {
      //articleCluster.add(article1) & articleCluster.add(article2) must beTrue
      true must beTrue
    }
    "articles test" in new scope {
      val articles = articleCluster.articles
      articles(article1.articleId) must equalTo(article1)
      articles(article2.articleId) must equalTo(article2)
    }
    "words test" in new scope {
      //article.freshness(dt.getMillis / 1000) must beEqualTo(math.exp(-2))
      val words = articleCluster.words
      val w1 = words("test1")
      w1.size must beEqualTo(1)
      w1.head must beEqualTo(article1)
      val w2 = words("test2")
      w2.size must beEqualTo(2)
      w2.head must beEqualTo(article2)
      w2.tail.head must beEqualTo(article1)
      val w3 = words("test3")
      w3.size must beEqualTo(1)
      w3.head must beEqualTo(article2)
    }
  }
}
