package net.javachallenge.entity

object ApiConversion {
  implicit def field(field: net.javachallenge.api.Field) = field.asInstanceOf[Field]
  implicit def game(game: net.javachallenge.api.Game) = game.asInstanceOf[Game]
  implicit def gameSetting(setting: net.javachallenge.api.GameSetting) = setting.asInstanceOf[GameSetting]
  implicit def player(player: net.javachallenge.api.Player) = player.asInstanceOf[Player]
  implicit def squad(squad: net.javachallenge.api.Squad) = squad.asInstanceOf[Squad]
  implicit def trianglePoint(trianglePoint: net.javachallenge.api.TrianglePoint) = trianglePoint.asInstanceOf[TrianglePoint]
  implicit def vein(vein: net.javachallenge.api.Vein) = vein.asInstanceOf[Vein]
}