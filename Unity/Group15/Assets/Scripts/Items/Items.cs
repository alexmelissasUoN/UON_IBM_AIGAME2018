﻿using System.Collections;
using System.Collections.Generic;
using UnityEngine;

//! Handle all items
public class Items {

    public Item sword;
    public Item shield;
    public Item armour;

    //! Create items based on the Player object's item attributes
    public Items(Player p)
    {
        sword = Item.NewItem("sword", p.sword);
        shield = Item.NewItem("shield", p.shield);
        armour = Item.NewItem("armour", p.armour);
    }

    //! Put items on player model, add their stats to Player
    public static void AttachItemsToPlayer(Items i, Player p) //also make this put the item icons onto player
    {
        if (PlayerSession.ps.player == null) return;
        p.hp += i.armour.hp;
        p.attack += i.sword.attack;
        p.defense += (i.shield.defense + i.armour.defense);
        return;
    }
}
