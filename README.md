# About This Mod

When's the last time you repaired your gear using an anvil? Probably not since the Mending enchantment was added to the game in 2016.

Repairing items with an anvil sucks. Mending is a band-aid solution which makes Minecraft less fun. This mod reworks these systems in an attempt to address my issues with them, while trying to keep the Vanilla feel. Importantly, it can be used on servers *without* requiring players to also install it on their end!

Below is a list of the changes this mod makes, along with some of the reasoning behind them.

## Changes to Anvil Repairing (And Renaming)

**1. NEVER AGAIN have an anvil tell you it's "too expensive"!**

You invest too heavily in acquiring your fully enchanted gear for the game to just not allow you to repair it anymore, especially at an arbitrary cost threshold and regardless of whether or not you actually have the theoretically required amount of levels. Now you can repair your gear indefinitely, and you don't need [the wiki](https://minecraft.wiki/w/Anvil_mechanics#Planning_the_enchanting_order) or external tools to figure out the optimally efficient way to combine your enchantments.
   
**2. Repair cost no longer increases each time you repair the same item.**

Instead, the number of enchantments an item has will affect how many xp levels it takes to repair it. This way the repair cost remains fixed and in your control, which could make for some interesting decisions about whether that extra enchantment is really necessary—maybe you'd rather not have Fortune III on your primary digging pickaxe to save yourself some levels when repairing it 🤔

**3. Cost of combining enchanted items is now based on the total number of enchantments on the item and the number of new enchantment levels being added.**

This is easier to understand than Minecraft's default "prior work penalty" system, and also means you don't need to consult the wiki, a flow chart, or an external tool to determine the cheapest way of getting all your enchantments onto an item.

**4. Netherite gear can be fully repaired with a single netherite ingot.**

Why this isn't how it works in vanilla Minecraft is completely beyond me. Charging *4 whole netherite ingots* to fully repair an item is absurd when 1 is enough to craft a brand new version of that same item.

**5. Renaming an item only ever costs 1 level.**

Renaming cost is no longer affected by the number of enchantments an item has or how often you've repaired it.

## Changes to Mending

**1. Mending no longer repairs gear using xp.**

Minecraft's Mending behaviour is incredibly overpowered and has many downsides:
 - It makes the anvil repair system entirely obsolete. Giving players the ability to circumvent the anvil's bad repair mechanics is arguably the reason why it exists in the first place.
 - It makes diamonds nearly worthless once you've got a full set of gear, and as a result there is almost zero reason to go mining after that point...in a game called MINEcraft.
 - The most consistent way of acquiring it involves mindlessly placing a lectern next to a villager (whom you've probably enslaved and locked in a small box/hole) over and over and over and over again until you finally get lucky. Boooooring! And *SO* tedious 🥱
 - The second most consistent way of acquiring it involves fishing for long periods of time or building an AFK fish farm. Sounds unappealing enough that I've never even bothered.
 - Once you have it, the most efficient way to mend your tools all the way is to sit in front of an xp farm. Also incredibly unexciting.
 - Armor effectively becomes unbreakable because xp will always repair it since you're wearing it (unlike tools which are only repaired by xp if you are actively holding them), and situations where your armor takes more damage than the xp you collect can repair are virtually non-existent. Not having your armor break accidentally is nice, but why should armor even have durability if it doesn't affect gameplay *at all* in the late game?
   
**2. Mending now reduces the number of materials required to repair gear in an anvil.**

Since this mod fixes the main problems with the anvil repair system, Mending no longer needs to exist as a substitute and can be repurposed. It now makes repairing your gear cheaper and more efficient by requiring less materials to fully repair an item. This takes Mending from a critical enchantment that you *must* have on all of your gear no matter how tedious it is to acquire, to an optional (but still useful) enchantment.

Maybe now you'll arrive at that end city and actually have a use for the Mending items you find because you didn't bother getting fully decked out in it quite yet!

**3. Netherite gear with Mending on it can be repaired using diamonds.**

At the netherite tier, the resource discount that Mending provides changes—instead of reducing the number of materials needed to fully repair the item (since a single netherite ingot will do that anyways), it allows you to use diamonds for the repair instead of netherite ingots. This does cost more levels per repair than usual, but provides an alternative option to mining for more ancient debris. I personally find caving in the overworld much more interesting and enjoyable than blowing up beds at the bottom of the nether over and over again.

**4. Armor with Mending won't break when its durability reaches 0.**

Other benefits aside, vanilla Mending is incredibly convenient to have on your armor because it *massively* reduces the risk of accidentally snapping it (since it keeps it fully repaired in almost all situations). This mod changes Mending's behaviour, but it would be a step backwards to remove this quality-of-life improvement. As an alternative, armor enchanted with Mending now stops working, but doesn't break—giving you the ability to repair it instead of permanently losing it—when its durability reaches 0.

This essentially mimics the way the elytra works, meaning that broken armor will not protect you or give you any of its enchantment effects. Until you repair it again, it's as though you aren't actually wearing it!

## Changes to XP

**The amount of XP required to reach the next level is now constant.**

Progressively increasing the amount of xp required to reach the next experience level (as is the case in Vanilla) might seem like a good idea—after all, that's how xp and leveling works in virtually every other game, right? However, Minecraft treats experience levels as a resource which you spend to enchant and repair things. The fact that those actions cost experience *levels* instead of *points* means that levels you spend when you have a lot of levels cost you *significantly* more xp than if you had fewer levels. To be efficient, you ideally never want to have more levels than exactly what is required to perform the desired action.

This mod removes level scaling so that the experience points required to reach the next level remain constant, which makes the 1st level just as valuable as the 30th (low levels represent more xp and high levels represent less compared to Vanilla). Gone are the days of hitting level 30 and having to decide between two annoying options: interrupt what you're doing to go enchant something or effectively waste any further xp you gain.

Yes, this means enchanting multiple items is cheaper than before (as long as you don't die in between) because getting back to level 30 from level 27 takes less xp, but you also can't use the less valuable levels (1-15) to repair and combine things at a massive discount. Also, being a bit cheaper could arguably make enchanting more competitive with villager trading again.

## Changes to Curses

**1. Cursed items are exempt from the enchantment level tax.**

The level cost for repairing or combining an item scales with how many enchantments it has (the "enchantment level tax"), but items with Curse of Vanishing and/or Curse of Binding on them are exempt from this increase. This means that instead of always jsut avoiding cursed items when possible, you might decide that the xp discount is worth the risk/inconvenience.

However, in Hardcore mode this exemption only applies to Curse of Binding, as Curse of Vanishing has no downside there (since dying means you can't keep playing on the world anymore anyways).

**2. Curse of Binding is no longer compatible with Mending.**

Curse of Binding prevents you from unequipping an item, and now that Mending prevents armor from fully breaking, having both on an item would mean it can only be removed by dying. This would also mean that in Hardcore mode or when playing with keepInventory enabled, removing the item would be impossible.

**3. Curse of Vanishing works even when keepInventory is enabled.**

In vanilla Minecraft, enabling the keepInventory game rule prevents you from dropping your items on death, including those with Curse of Vanishing on them. This doesn't make any sense, and would provide the enchantment level tax exemption without any risk.