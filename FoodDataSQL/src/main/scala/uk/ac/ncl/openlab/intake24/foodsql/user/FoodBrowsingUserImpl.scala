package uk.ac.ncl.openlab.intake24.foodsql.user

import scala.Right

import anorm.Macro
import anorm.NamedParameter.symbol
import anorm.SQL
import anorm.sqlToSimple
import uk.ac.ncl.openlab.intake24.UserCategoryContents
import uk.ac.ncl.openlab.intake24.UserCategoryHeader
import uk.ac.ncl.openlab.intake24.UserFoodHeader
import uk.ac.ncl.openlab.intake24.foodsql.FirstRowValidation
import uk.ac.ncl.openlab.intake24.foodsql.FirstRowValidationClause
import uk.ac.ncl.openlab.intake24.foodsql.SqlDataService
import uk.ac.ncl.openlab.intake24.foodsql.SqlResourceLoader
import uk.ac.ncl.openlab.intake24.services.fooddb.errors.LocalLookupError
import uk.ac.ncl.openlab.intake24.services.fooddb.errors.LocaleError
import uk.ac.ncl.openlab.intake24.services.fooddb.user.FoodBrowsingService

import uk.ac.ncl.openlab.intake24.services.fooddb.errors.LookupError
import uk.ac.ncl.openlab.intake24.services.fooddb.errors.LookupError
import uk.ac.ncl.openlab.intake24.foodsql.shared.SuperCategoriesQueries

trait FoodBrowsingUserImpl extends FoodBrowsingService with SqlDataService with SqlResourceLoader with FirstRowValidation with SuperCategoriesQueries {

  private lazy val rootCategoriesQuery = sqlFromResource("user/root_categories.sql")

  private case class UserCategoryHeaderRow(code: String, description: String, local_description: Option[String]) {
    def mkUserHeader = UserCategoryHeader(code, local_description.getOrElse(description))
  }

  def getRootCategories(locale: String): Either[LocaleError, Seq[UserCategoryHeader]] = tryWithConnection {
    implicit conn =>
      val result = SQL(rootCategoriesQuery).on('locale_id -> locale).executeQuery()

      parseWithLocaleValidation(result, Macro.namedParser[UserCategoryHeaderRow].+)(Seq(FirstRowValidationClause("code", () => Right(List())))).right.map {
        _.map {
          _.mkUserHeader
        }
      }
  }

  private lazy val categoryFoodContentsQuery = sqlFromResource("user/category_contents_foods.sql")

  private case class UserFoodHeaderRow(code: String, description: String, local_description: Option[String]) {
    def mkUserHeader = UserFoodHeader(code, local_description.getOrElse(description))
  }

  private def categoryFoodContentsImpl(code: String, locale: String)(implicit conn: java.sql.Connection): Either[LocalLookupError, Seq[UserFoodHeader]] = {

    val result = SQL(categoryFoodContentsQuery).on('category_code -> code, 'locale_id -> locale).executeQuery()

    parseWithLocaleAndCategoryValidation(code, result, Macro.namedParser[UserFoodHeaderRow].+)(Seq(FirstRowValidationClause("code", () => Right(List())))).right.map {
      _.map {
        _.mkUserHeader
      }
    }
  }

  private lazy val categorySubcategoryContentsQuery = sqlFromResource("user/category_contents_subcategories.sql")

  private def categorySubcategoryContentsImpl(code: String, locale: String)(implicit conn: java.sql.Connection): Either[LocalLookupError, Seq[UserCategoryHeader]] = {
    val result = SQL(categorySubcategoryContentsQuery).on('category_code -> code, 'locale_id -> locale).executeQuery()

    parseWithLocaleAndCategoryValidation(code, result, Macro.namedParser[UserCategoryHeaderRow].+)(Seq(FirstRowValidationClause("code", () => Right(List())))).right.map {
      _.map {
        _.mkUserHeader
      }
    }
  }

  def getCategoryContents(code: String, locale: String): Either[LocalLookupError, UserCategoryContents] = tryWithConnection {
    implicit conn =>      
      for (
        foods <- categoryFoodContentsImpl(code, locale).right;
        subcategories <- categorySubcategoryContentsImpl(code, locale).right
      ) yield {
        UserCategoryContents(foods, subcategories)
      }
  }

  def getFoodAllCategories(code: String): Either[LookupError, Set[String]] = tryWithConnection {
    implicit conn => getFoodAllCategoriesCodesQuery(code)
  }

  def getCategoryAllCategories(code: String): Either[LookupError, Set[String]] = tryWithConnection {
    implicit conn => getCategoryAllCategoriesCodesQuery(code)
  }
}