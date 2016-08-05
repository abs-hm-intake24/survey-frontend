package uk.ac.ncl.openlab.intake24.foodsql.user

import uk.ac.ncl.openlab.intake24.services.fooddb.user.BrandNamesService
import uk.ac.ncl.openlab.intake24.foodsql.SqlDataService
import anorm._
import anorm.SqlParser.str
import uk.ac.ncl.openlab.intake24.services.fooddb.errors.LocalFoodCodeError
import uk.ac.ncl.openlab.intake24.services.fooddb.errors.UndefinedCode
import uk.ac.ncl.openlab.intake24.services.fooddb.errors.UndefinedLocale
import uk.ac.ncl.openlab.intake24.services.fooddb.errors.DatabaseError

trait BrandNamesUserImpl extends BrandNamesService with SqlDataService {
  
  private case class Validation(foodCodeValid: Boolean, localeIdValid: Boolean, hasRows: Boolean)
  
  def brandNames(foodCode: String, locale: String): Either[LocalFoodCodeError, Seq[String]] = tryWithConnection {
    implicit conn =>
      
      // see http://stackoverflow.com/a/38793141/622196 for explanation
      
      val query = """|WITH v AS ( 
                     |  SELECT (SELECT code FROM foods WHERE code={food_code}) AS food_code, 
                     |  SELECT (SELECT id FROM locales WHERE id={locale_id}) AS locale_id
                     |)
                     |SELECT v.food_code, v.locale_id, brands.name FROM 
                     |v LEFT JOIN brands ON v.food_code = brands.food_code AND v.locale_id = brands.locale_id""".stripMargin
                     
                     
      val result = SQL(query).on('food_code -> foodCode, 'locale_id -> locale).executeQuery()
      
      val parser = SqlParser.str("name").+
      
      result.withResult {
        cursorOpt =>
          val row = cursorOpt.get.row
          
          val columns = row.asMap
          
          columns("locale_id") match {
            case None => ???
            case _ => ???
          }
          
          val foodCode = row.
          val localeId = row("locale_id")(Column.columnToOption[String])
                   
      }
      
      result.resultSet.acquireFor { 
        rs =>
          rs.first()
          Validation(rs.getString(0) != null, rs.getString(1) != null, rs.getString(2) != null)
      } match {
        case Left(exceptions) => Left(DatabaseError(exceptions.head.getMessage, exceptions.head))
        case Right(Validation(foodCodeValid, localeIdValid, hasRows)) => {
          if (!foodCodeValid)
            Left(UndefinedCode)
          else if (!localeIdValid)
            Left(UndefinedLocale)
          else if (!hasRows)
            Right(Seq())
          else
            Right(result.as(SqlParser.str("name").+))
        }
      }
  }
}
