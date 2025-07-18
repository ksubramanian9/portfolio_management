package domain

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class PortfolioSpec extends AnyFlatSpec with Matchers {
  "A Portfolio" should "be instantiable" in {
    val portfolio = Portfolio("1", "Test")
    portfolio.id shouldEqual "1"
  }
}
