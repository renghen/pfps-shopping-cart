package shop.http.routes.admin

import cats.effect.Sync
import io.circe.refined._
import org.http4s._
import org.http4s.dsl.Http4sDsl
import org.http4s.server._
import shop.algebras.Categories
import shop.domain.category._
import shop.http.auth.users.AdminUser
import shop.http.decoder._
import shop.http.json._

final class AdminCategoryRoutes[F[_]: Sync](
    categories: Categories[F]
) extends Http4sDsl[F] {

  private[admin] val prefixPath = "/categories"

  private val httpRoutes: AuthedRoutes[AdminUser, F] =
    AuthedRoutes.of {
      case ar @ POST -> Root as _ =>
        ar.req.decodeR[CategoryParam] { c =>
          Created(categories.create(c.toDomain))
        }
    }

  def routes(authMiddleware: AuthMiddleware[F, AdminUser]): HttpRoutes[F] = Router(
    prefixPath -> authMiddleware(httpRoutes)
  )

}
