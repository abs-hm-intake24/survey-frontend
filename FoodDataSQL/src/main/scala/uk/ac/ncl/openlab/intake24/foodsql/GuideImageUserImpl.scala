package uk.ac.ncl.openlab.intake24.foodsql

import uk.ac.ncl.openlab.intake24.GuideImage
import anorm._
import uk.ac.ncl.openlab.intake24.GuideImageWeightRecord
import uk.ac.ncl.openlab.intake24.services.fooddb.user.GuideImageService
import uk.ac.ncl.openlab.intake24.services.fooddb.errors.ResourceError
import uk.ac.ncl.openlab.intake24.services.fooddb.errors.UndefinedCode
import uk.ac.ncl.openlab.intake24.services.fooddb.errors.ResourceNotFound

trait GuideImageUserImpl extends GuideImageService with SqlDataService {

  protected case class GuideResultRow(image_description: String, object_id: Int, object_description: String, weight: Double)

  def guideDef(id: String): Either[ResourceError, GuideImage] = tryWithConnection {
    implicit conn =>
      val query =
        """|SELECT guide_images.description as image_description, object_id, 
         |       guide_image_weights.description as object_description, weight 
         |FROM guide_images JOIN guide_image_weights ON guide_images.id = guide_image_id 
         |WHERE guide_images.id = {id} ORDER BY guide_image_weights.object_id""".stripMargin

      val result = SQL(query).on('id -> id).executeQuery().as(Macro.namedParser[GuideResultRow].*)

      if (result.isEmpty)
        Left(ResourceNotFound)
      else {
        val weights = result.map(row => GuideImageWeightRecord(row.object_description, row.object_id, row.weight))
        Right(GuideImage(id, result.head.image_description, weights))
      }
  }
}
