package com.under_hair.topicwords

import org.specs2.mutable._
import org.joda.time.DateTime

/**
 * Created with IntelliJ IDEA.
 * User: mastakeu
 * Date: 2013/01/27
 * Time: 18:03
 * To change this template use File | Settings | File Templates.
 */
class ArticleSpec extends Specification {
  val dt = new DateTime
  val article = new Article(
    "article01",
    dt.minusDays(1).getMillis / 1000,
    List("test1", "test2")
  )

  "Article" should {
    "articleId test" in {
      article.articleId must beEqualTo("article01")
    }
    "freshness test" in {
      article.freshness(dt.getMillis / 1000) must beEqualTo(math.exp(-2))
    }
  }
}
