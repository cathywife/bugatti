package models.conf

import play.api.Play.current
import models.PlayCache

import scala.slick.driver.MySQLDriver.simple._
import scala.slick.jdbc.JdbcBackend

/**
 * 项目模板描述表
 */
case class TemplateItem(id: Option[Int], tid: Option[Int], itemName: String, itemDesc: Option[String], default: Option[String], order: Int)
class TemplateItemTable(tag: Tag) extends Table[TemplateItem](tag, "template_item") {
  def id = column[Int]("id", O.PrimaryKey, O.AutoInc)
  def tid = column[Int]("tid") // 模板编号
  def itemName = column[String]("item_name", O.NotNull) // 字段定义的名称
  def itemDesc = column[String]("item_desc", O.Nullable) // 字段定义的描述
  def default = column[String]("item_default", O.Nullable)
  def order = column[Int]("order", O.NotNull, O.Default(0)) // 字段排序

  override def * = (id.?, tid.?, itemName, itemDesc.?, default.?, order) <> (TemplateItem.tupled, TemplateItem.unapply _)
  def idx = index("idx_tid", tid)
  def idx_name = index("idx_name", (tid, itemName), unique = true)
}
object TemplateItemHelper extends PlayCache {
  import models.AppDB._

  val qItem = TableQuery[TemplateItemTable]

  def findById(id: Int) = db withSession { implicit session =>
    qItem.where(_.id is id).firstOption
  }

  def findByTid(tid: Int): List[TemplateItem] = db withSession { implicit session =>
    qItem.where(_.tid is tid).list
  }

  def create(templateInfo: TemplateItem) = db withSession { implicit session =>
    create_(templateInfo)
  }

  def create_(templateInfo: TemplateItem)(implicit session: JdbcBackend#Session) = {
    qItem.insert(templateInfo)(session)
  }

  def update(id: Int, templateInfo: TemplateItem) = db withSession { implicit session =>
    val info2update = templateInfo.copy(Some(id))
    qItem.where(_.id is id).update(info2update)
  }

  def deleteByTid_(tid: Int)(implicit session: JdbcBackend#Session) = {
    qItem.where(_.tid is tid).delete
  }
}
