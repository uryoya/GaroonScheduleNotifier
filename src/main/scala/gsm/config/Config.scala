package gsm.config

import gsm.config.Config._

final case class Config(cybozu: Cybozu, slack: Slack)

object Config {
  final case class Cybozu(loginName: String, password: String, host: String, installId: String)
  final case class Slack(webhookUrl: String)
}
