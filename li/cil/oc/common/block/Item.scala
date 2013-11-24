package li.cil.oc.common.block

import java.util
import li.cil.oc.Config
import li.cil.oc.common.tileentity
import net.minecraft.block.Block
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.ItemBlock
import net.minecraft.item.ItemStack
import net.minecraft.world.World
import net.minecraftforge.common.ForgeDirection

class Item(id: Int) extends ItemBlock(id) {
  setHasSubtypes(true)

  override def addInformation(stack: ItemStack, player: EntityPlayer, tooltip: util.List[_], advanced: Boolean) {
    super.addInformation(stack, player, tooltip, advanced)
    Block.blocksList(getBlockID) match {
      case delegator: Delegator[_] => delegator.addInformation(getMetadata(stack.getItemDamage), player, tooltip.asInstanceOf[util.List[String]], advanced)
      case _ =>
    }
  }

  override def getMetadata(itemDamage: Int) = itemDamage

  override def getUnlocalizedName = Config.namespace + "block"

  override def getUnlocalizedName(item: ItemStack) =
    Block.blocksList(getBlockID) match {
      case delegator: Delegator[_] => Config.namespace + "block." + delegator.getUnlocalizedName(item.getItemDamage)
      case block => block.getUnlocalizedName
    }

  override def isBookEnchantable(a: ItemStack, b: ItemStack) = false

  override def placeBlockAt(item: ItemStack, player: EntityPlayer, world: World, x: Int, y: Int, z: Int, side: Int, hitX: Float, hitY: Float, hitZ: Float, metadata: Int) = {
    if (super.placeBlockAt(item, player, world, x, y, z, side, hitX, hitY, hitZ, metadata)) {
      // If it's a rotatable block try to make it face the player.
      world.getBlockTileEntity(x, y, z) match {
        case keyboard: tileentity.Keyboard =>
          keyboard.setFromEntityPitchAndYaw(player)
          keyboard.setFromFacing(ForgeDirection.getOrientation(side))
        case rotatable: tileentity.Rotatable =>
          rotatable.setFromEntityPitchAndYaw(player)
          if (!rotatable.isInstanceOf[tileentity.RobotProxy]) {
            rotatable.invertRotation()
          }
        case _ => // Ignore.
      }
      true
    }
    else false
  }
}