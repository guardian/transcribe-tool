
import com.gu.pandomainauth.PanDomainAuthSettingsRefresher
import config.{Config, LogConfig}
import play.api.ApplicationLoader.Context
import play.api.libs.ws.ahc.AhcWSComponents
import play.api.BuiltInComponentsFromContext
import controllers.{AssetsComponents, Login}
import play.api.routing.Router
import router.Routes
import play.filters.HttpFiltersComponents

class AppComponents(context: Context)
  extends BuiltInComponentsFromContext(context) with AhcWSComponents with AssetsComponents with HttpFiltersComponents {


  val config = new Config(context.initialConfiguration)

  val panDomainSettings = new PanDomainAuthSettingsRefresher(
    domain = config.pandaDomain,
    system = config.appName,
    actorSystem = actorSystem,
    awsCredentialsProvider = config.awsCredentialsProvider
  )


  val logger = new LogConfig(config)
  lazy val router: Router = new Routes(httpErrorHandler, appController, healthcheckController, loginController, assets)
  lazy val appController = new controllers.App(wsClient, controllerComponents, panDomainSettings, config, assets)
  lazy val loginController = new Login(wsClient, controllerComponents, panDomainSettings, config)
  lazy val healthcheckController = new controllers.Healthcheck(controllerComponents)
//  lazy val frontendController = new controllers.FrontendController(assets, httpErrorHandler, context.initialConfiguration,controllerComponents)
}


