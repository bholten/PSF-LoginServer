// Copyright (c) 2017 PSForever
package net.psforever.objects.serverobject.resourcesilo

import akka.actor.{ActorContext, Props}
import net.psforever.objects.{GlobalDefinitions, Player, Vehicle}
import net.psforever.objects.serverobject.structures.Amenity
import net.psforever.packet.game.UseItemMessage

class ResourceSilo extends Amenity {
  private var chargeLevel : Int = 0
  private val maximumCharge : Int = 1000
  // For the flashing red light on top of the NTU silo on
  private var lowNtuWarningOn : Int = 0

  // For the NTU display bar
  private var capacitorDisplay : Long = 0

  def ChargeLevel : Int = chargeLevel

  // Do not call directly. Use ResourceSilo.UpdateChargeLevel message to handle logic such as low ntu warnings
  def ChargeLevel_=(charge: Int) : Int = {
    if(charge < 0 ) {
      chargeLevel = 0
    } else if (charge > maximumCharge) {
      chargeLevel = maximumCharge
    } else {
      chargeLevel = charge
    }
    ChargeLevel
  }

  def MaximumCharge : Int = maximumCharge

  def LowNtuWarningOn : Int = lowNtuWarningOn
  def LowNtuWarningOn_=(enabled: Int) : Int = {
    lowNtuWarningOn = enabled
    LowNtuWarningOn
  }

  def CapacitorDisplay : Long = capacitorDisplay
  def CapacitorDisplay_=(value: Long) : Long = {
    capacitorDisplay = value
    CapacitorDisplay
  }

  def Definition : ResourceSiloDefinition = GlobalDefinitions.resource_silo

  def Use(player: Player, msg : UseItemMessage) : ResourceSilo.Exchange = {
    ResourceSilo.ChargeEvent()
  }
}


object ResourceSilo {

  final case class Use(player: Player, msg : UseItemMessage)
  final case class UpdateChargeLevel(amount: Int)
  final case class LowNtuWarning(enabled: Int)
  sealed trait Exchange
  final case class ChargeEvent() extends Exchange
  final case class ResourceSiloMessage(player: Player, msg : UseItemMessage, response : Exchange)


  /**
    * Overloaded constructor.
    * @return the `Resource Silo` object
    */
  def apply() : ResourceSilo = {
    new ResourceSilo()
  }

  /**
    * Instantiate an configure a `Resource Silo` object
    * @param id the unique id that will be assigned to this entity
    * @param context a context to allow the object to properly set up `ActorSystem` functionality;
    *                not necessary for this object, but required by signature
    * @return the `Locker` object
    */
  def Constructor(id : Int, context : ActorContext) : ResourceSilo = {
    val obj = ResourceSilo()
    obj.Actor = context.actorOf(Props(classOf[ResourceSiloControl], obj), s"${obj.Definition.Name}_$id")
    obj.Actor ! "startup"
    obj
  }
}