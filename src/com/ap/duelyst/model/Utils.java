package com.ap.duelyst.model;

import com.ap.duelyst.model.buffs.*;
import com.ap.duelyst.model.buffs.traget.EffectType;
import com.ap.duelyst.model.buffs.traget.RangeType;
import com.ap.duelyst.model.buffs.traget.SideType;
import com.ap.duelyst.model.buffs.traget.TargetType;
import com.ap.duelyst.model.cards.*;
import com.ap.duelyst.model.items.CollectableItem;
import com.ap.duelyst.model.items.Item;
import com.ap.duelyst.model.items.UsableItem;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class Utils {
    private static List<Account> accounts = new ArrayList<>();
    private static Shop shop;
    private static List<int[]> nums = new ArrayList<>();

    static {
        nums.add(new int[]{0, 0});
        nums.add(new int[]{0, 1});
        nums.add(new int[]{0, 2});
        nums.add(new int[]{0, 3});
        nums.add(new int[]{1, 0});
        nums.add(new int[]{1, 1});
        nums.add(new int[]{1, 2});
        nums.add(new int[]{1, 3});
        nums.add(new int[]{2, 1});
        nums.add(new int[]{2, 2});
        nums.add(new int[]{2, 3});
        nums.add(new int[]{3, 0});
        nums.add(new int[]{3, 1});
        nums.add(new int[]{3, 2});
        nums.add(new int[]{3, 3});
        nums.add(new int[]{4, 0});
        nums.add(new int[]{4, 1});
        nums.add(new int[]{4, 2});
        nums.add(new int[]{4, 3});
    }

    static {
        List<Card> cards = new ArrayList<>();
        List<Item> items = new ArrayList<>();

//        spells
        Buff buff = new DisarmBuff(-1, false, TargetType.MINION, SideType.ENEMY,
                RangeType.ONE);
        Spell spell = new Spell("total-disarm", 1000, 0,
                "disarms enemy minion till the end of the game", buff);
        cards.add(spell);


        buff = new DispelBuff(1, false, TargetType.MINION, SideType.ALL,
                RangeType.SQUARE2);
        spell = new Spell("aria-dispel", 1500, 2, "dispels buffs for all", buff);
        cards.add(spell);


        buff = new PowerBuff(-1, false, TargetType.MINION, SideType.ALLY, RangeType.ONE,
                EffectType.ATTACK_POWER, 2);
        spell = new Spell("empower", 250, 1, "increases ap of ally minion by 2", buff);
        cards.add(spell);


        buff = new AttackBuff(1, false, TargetType.MINION, SideType.ENEMY,
                RangeType.ONE, 4);
        spell = new Spell("fireball", 400, 1, "damages enemy minion 4 points", buff);
        cards.add(spell);


        buff = new PowerBuff(Integer.MAX_VALUE, false, TargetType.HERO, SideType.ALLY,
                RangeType.ONE,
                EffectType.ATTACK_POWER, 4);
        spell = new Spell("god-strength", 450, 2, "increases ap of ally hero by 4", buff);
        cards.add(spell);


        Buff buff1 = new WeaknessBuff(-1, false, TargetType.MINION, SideType.ENEMY,
                RangeType.ONE,
                EffectType.HEALTH, 2);
        buff = new CellBuff(2, false, TargetType.CELL, SideType.ALL, RangeType.SQUARE2,
                buff1);
        spell = new Spell("hellfire", 450, 2, "increases ap of ally hero by 4", buff);
        cards.add(spell);


        buff = new AttackBuff(1, false, TargetType.HERO, SideType.ENEMY, RangeType.ONE,
                8);
        spell = new Spell("lightning-bolt", 1250, 2, "damages enemy hero 4 points", buff);
        cards.add(spell);


        buff = new PoisonBuff(3, false, TargetType.HERO_MINION, SideType.ENEMY,
                RangeType.ONE);
        buff1 = new CellBuff(1, false, TargetType.CELL, SideType.ALL, RangeType.SQUARE3
                , buff);
        spell = new Spell("poison-lake", 900, 5, "adds poison effect in a square size " +
                "3", buff1);
        cards.add(spell);


        buff = new DisarmBuff(Integer.MAX_VALUE, false, TargetType.MINION,
                SideType.ALLY, RangeType.ONE);
        buff1 = new PowerBuff(3, false, TargetType.MINION, SideType.ALLY, RangeType.ONE,
                EffectType.ATTACK_POWER, 4);
        spell = new Spell("madness", 650, 0, "increases ally minion ap but disarms him"
                , buff, buff1);
        cards.add(spell);


        buff = new DisarmBuff(1, false, TargetType.MINION, SideType.ENEMY,
                RangeType.ALL_BOARD);
        spell = new Spell("all-disarm", 2000, 9, "disarms all enemy minions", buff);
        cards.add(spell);


        buff = new PoisonBuff(4, false, TargetType.MINION, SideType.ENEMY,
                RangeType.ALL_BOARD);
        spell = new Spell("all-poison", 1500, 8, "poisons all enemy minions", buff);
        cards.add(spell);


        buff = new DispelBuff(1, false, TargetType.MINION, SideType.ALL, RangeType.ONE);
        spell = new Spell("dispel", 2100, 0, "dispels buffs for one enemy or ally", buff);
        cards.add(spell);


        buff = new WeaknessBuff(-1, false, TargetType.MINION, SideType.ALLY,
                RangeType.ONE,
                EffectType.HEALTH, 6);
        buff1 = new HolyBuff(3, false, TargetType.MINION, SideType.ALLY, RangeType.ONE,
                2);
        spell = new Spell("health-with-benefit", 2250, 0,
                "adds holly buff to a minion for 3 rounds and 2 points but weakens him " +
                        "by 6 points", buff, buff1);
        cards.add(spell);


        buff = new PowerBuff(-1, false, TargetType.MINION, SideType.ALLY, RangeType.ONE,
                EffectType.ATTACK_POWER, 6);
        spell = new Spell("power-up", 2500, 2, "increases ally minion ap by 6 points",
                buff);
        cards.add(spell);


        buff = new PowerBuff(-1, true, TargetType.MINION, SideType.ALLY,
                RangeType.ALL_BOARD,
                EffectType.ATTACK_POWER, 2);
        spell = new Spell("all-power", 2000, 4, "increases all ally minions ap by 2 " +
                "points", buff);
        cards.add(spell);


        buff = new AttackBuff(1, false, TargetType.MINION, SideType.ENEMY,
                RangeType.ALL_IN_ONE_COLUMN, 6);
        spell = new Spell("all-attack", 1500, 4, "damages all enemy minions in one " +
                "column", buff);
        cards.add(spell);


        buff = new WeaknessBuff(-1, false, TargetType.MINION, SideType.ENEMY,
                RangeType.ONE,
                EffectType.ATTACK_POWER, 4);
        spell = new Spell("weakening", 1000, 1, "weakens enemy minion's ap by 4 points"
                , buff);
        cards.add(spell);


        buff = new WeaknessBuff(-1, false, TargetType.MINION, SideType.ALLY,
                RangeType.ONE,
                EffectType.HEALTH, 6);
        buff1 = new PowerBuff(-1, false, TargetType.MINION, SideType.ALLY, RangeType.ONE,
                EffectType.ATTACK_POWER, 8);
        spell = new Spell("sacrifice", 1600, 2,
                "weakens ally minion hp by 6 but increases it's ap by 8", buff, buff1);
        cards.add(spell);


        buff = new KingsGuardBuff(1, false, TargetType.MINION, SideType.ENEMY,
                RangeType.AROUND8);
        buff.setRandom(true);
        spell = new Spell("kings-guard", 1750, 9, "kills one random enemy minion in " +
                "radius 8 of ally hero", buff);
        cards.add(spell);


        buff = new StunBuff(2, false, TargetType.MINION, SideType.ENEMY, RangeType.ONE);
        spell = new Spell("shock", 1200, 1, "stuns an enemy minion for 2 rounds", buff);
        cards.add(spell);


//        minions
        Minion minion = new Minion("persian-archer", 300, 2, 6, 4, AttackType.RANGED, 7
                , null, null);
        minion.setFileName("f5_ragnoramk2");
        cards.add(minion);


        buff = new StunBuff(1, false, TargetType.MINION, SideType.ENEMY, RangeType.ONE);
        spell = new Spell("SP", 0, 0, "stuns enemy on attack for two rounds", buff);
        minion = new Minion("persian-swordsman", 400, 2, 6, 4, AttackType.MELEE, 0,
                spell, ActivationTime.ON_ATTACK);
        minion.setFileName("f6_altgeneraltier2");
        cards.add(minion);


        minion = new Minion("persian-spear-man", 500, 1, 5, 3, AttackType.HYBRID, 3,
                null, null);
        minion.setFileName("boss_chaosknight");
        cards.add(minion);


        minion = new Minion("persian-cavalry", 200, 4, 10, 6, AttackType.MELEE, 0, null
                , null);
        minion.setFileName("f5_altgeneraltier2");
        cards.add(minion);


        spell = new Spell("sp", 0, 0, "adds 5 more damage by every attack to a certain " +
                "minion");
        minion = new Minion("persian-hero", 600, 9, 24, 6, AttackType.MELEE, 0, spell,
                ActivationTime.ON_ATTACK);
        minion.setFileName("f4_maehvmk2");
        cards.add(minion);


        minion = new Minion("persian-commander", 800, 7, 12, 4, AttackType.MELEE, 0,
                null, ActivationTime.COMBO);
        minion.setFileName("f1_bromemk2");
        cards.add(minion);


        minion = new Minion("transoxianain-archer", 500, 1, 3, 4, AttackType.RANGED, 5,
                null, null);
        minion.setFileName("neutral_ironclad");
        cards.add(minion);


        minion = new Minion("transoxianain-sling-man", 600, 1, 4, 2, AttackType.RANGED,
                7, null, null);
        minion.setFileName("f2_shidaimk2");
        cards.add(minion);


        minion = new Minion("transoxianain-spear-man", 600, 1, 4, 4, AttackType.HYBRID,
                3, null, null);
        minion.setFileName("f5_tier2general");
        cards.add(minion);


        buff = new DisarmBuff(1, false, TargetType.MINION, SideType.ENEMY, RangeType.ONE);
        buff1 = new PoisonBuff(4, false, TargetType.MINION, SideType.ENEMY,
                RangeType.ONE);
        spell = new Spell("sp", 0, 0, "disarms enemy 1 round and poisons him for 4 " +
                "rounds", buff, buff1);
        minion = new Minion("transoxianain-spy", 700, 4, 6, 6, AttackType.MELEE, 0,
                spell, ActivationTime.ON_ATTACK);
        minion.setFileName("f2_tier2general");
        cards.add(minion);


        minion = new Minion("transoxianain-trench-raider", 450, 2, 3, 10,
                AttackType.MELEE, 0, null, null);
        minion.setFileName("f3_altgeneraltier2");
        cards.add(minion);


        minion = new Minion("transoxianain-prince", 800, 6, 6, 10, AttackType.MELEE, 0,
                null, ActivationTime.COMBO);
        minion.setFileName("f4_3rdgeneral");
        cards.add(minion);


        minion = new Minion("black-beast", 300, 9, 14, 10, AttackType.HYBRID, 7, null,
                null);
        minion.setFileName("boss_antiswarm");
        cards.add(minion);


        minion = new Minion("giant-rock-thrower", 300, 9, 12, 12, AttackType.RANGED, 7,
                null, null);
        minion.setFileName("f2_altgeneraltier2");
        cards.add(minion);


        //todo eagle
        /*buff = new PowerBuff(Integer.MAX_VALUE,false,TargetType.HERO_MINION,);
        spell = new;
        minion = new Minion("eagle", 200, 2, 0, 2, AttackType.RANGED, 3, null, null);
        cards.add(minion);*/


        minion = new Minion("cavalry-beast", 300, 6, 16, 8, AttackType.MELEE, 0, null,
                null);
        minion.setFileName("f3_tier2general");
        cards.add(minion);


        buff = new AttackBuff(1, false, TargetType.MINION, SideType.ENEMY,
                RangeType.AROUND8, 2);
        spell = new Spell("sp", 0, 0, "damages enemy minions in it's 8cell radius 2 " +
                "points", buff);
        minion = new Minion("one-eyed-giant", 500, 7, 12, 11, AttackType.HYBRID, 3,
                spell, ActivationTime.ON_DEATH);
        minion.setFileName("neutral_bloodletter");
        cards.add(minion);


        buff = new PoisonBuff(3, false, TargetType.MINION, SideType.ENEMY, RangeType.ONE);
        spell = new Spell("sp", 0, 0, "poisons enemy for 3 rounds", buff);
        minion = new Minion("poisonous-snake", 300, 4, 5, 6, AttackType.RANGED, 4,
                spell, ActivationTime.ON_ATTACK);
        minion.setFileName("neutral_moltengolem");
        cards.add(minion);


        minion = new Minion("fire-dragon", 250, 5, 9, 5, AttackType.RANGED, 4, null,
                null);
        minion.setFileName("f4_tier2general");
        cards.add(minion);


        minion = new Minion("fierce-lion", 600, 2, 1, 8, AttackType.MELEE, 0, null,
                ActivationTime.ON_ATTACK);
        minion.setDisableEnemyHolyBuff(true);
        minion.setFileName("f6_buildlegendary");
        cards.add(minion);


        buff = new HolyBuff(Integer.MAX_VALUE, false, TargetType.MINION, SideType.ENEMY
                , RangeType.AROUND2, -1);
        spell = new Spell("sp", 0, 0, "damages enemy minions in it's 2cell radius 1 " +
                "more point", buff);
        minion = new Minion("gigantic-snake", 500, 8, 14, 7, AttackType.RANGED, 5,
                spell, ActivationTime.ON_SPAWN);
        minion.setFileName("f3_ciphyronmk2");
        cards.add(minion);


        buff = new HistoryBuff(false, TargetType.MINION, SideType.ENEMY, RangeType.ONE,
                4, 6);
        spell = new Spell("sp", 0, 0, "damages enemy minion after attack by 6 and 4 in " +
                "next rounds", buff);
        minion = new Minion("White-wolf", 400, 5, 8, 2, AttackType.MELEE, 0, spell,
                ActivationTime.ON_ATTACK);
        minion.setFileName("f6_frostiva");
        cards.add(minion);


        buff = new HistoryBuff(false, TargetType.MINION, SideType.ENEMY, RangeType.ONE,
                8);
        spell = new Spell("sp", 0, 0, "damages enemy minion after attack by 8 in next " +
                "round", buff);
        minion = new Minion("panther", 400, 4, 6, 2, AttackType.MELEE, 0, spell,
                ActivationTime.ON_ATTACK);
        minion.setFileName("boss_invader");
        cards.add(minion);


        buff = new HistoryBuff(false, TargetType.MINION, SideType.ENEMY, RangeType.ONE,
                6);
        spell = new Spell("sp", 0, 0, "damages enemy minion after attack by 6 in next " +
                "round", buff);
        minion = new Minion("wolf", 400, 3, 6, 1, AttackType.MELEE, 0, spell,
                ActivationTime.ON_ATTACK);
        minion.setFileName("f1_altgeneraltier2");
        cards.add(minion);


        buff = new PowerBuff(1, false, TargetType.MINION, SideType.ALLY,
                RangeType.AROUND8_AND_SELF,
                EffectType.ATTACK_POWER
                , 2);
        buff1 = new WeaknessBuff(1, false, TargetType.MINION, SideType.ALLY,
                RangeType.AROUND8_AND_SELF,
                EffectType.HEALTH, 1);
        spell = new Spell("sp", 0, 0,
                "adds to himself and in 8 radius minion allys 2 ap and 1 hp weakness " +
                        "for 1 round", buff, buff1);
        minion = new Minion("witch", 550, 4, 5, 4, AttackType.RANGED, 3, spell,
                ActivationTime.PASSIVE);
        minion.setFileName("f6_ilenamk2");
        cards.add(minion);


        buff = new PowerBuff(-1, true, TargetType.MINION, SideType.ALLY,
                RangeType.AROUND8_AND_SELF,
                EffectType.ATTACK_POWER
                , 2);
        buff1 = new HolyBuff(-1, true, TargetType.MINION, SideType.ALLY,
                RangeType.AROUND8_AND_SELF, 1);
        spell = new Spell("sp", 0, 0,
                "adds to himself and in 8 radius minion allys 2 ap and 1 holy buff " +
                        "continues", buff, buff1);
        minion = new Minion("high-witch", 550, 6, 6, 6, AttackType.RANGED, 5, spell,
                ActivationTime.PASSIVE);
        minion.setFileName("f5_rancour");
        cards.add(minion);


        buff = new PowerBuff(-1, true, TargetType.MINION, SideType.ALLY,
                RangeType.ALL_BOARD,
                EffectType.ATTACK_POWER, 1);
        spell = new Spell("sp", 0, 0, "increases all ally minions ap by 1 point", buff);
        minion = new Minion("ghost", 500, 5, 10, 4, AttackType.RANGED, 4, spell,
                ActivationTime.PASSIVE);
        minion.setFileName("boss_treatdrake");
        cards.add(minion);


        spell = new Spell("sp", 0, 0, "this minion cant be disarmed");
        minion = new Minion("wild-pig", 500, 6, 10, 14, AttackType.MELEE, 0, spell,
                ActivationTime.ON_DEFEND);
        minion.setCanBeDisarmed(false);
        minion.setFileName("boss_spelleater");
        cards.add(minion);

        spell = new Spell("sp", 0, 0, "this minion cant be poisoned");
        minion = new Minion("piran", 400, 8, 20, 12, AttackType.MELEE, 0, spell,
                ActivationTime.ON_DEFEND);
        minion.setCanBePoisoned(false);
        minion.setFileName("f1_3rdgeneral");
        cards.add(minion);

        spell = new Spell("sp", 0, 0, "this minion is immune to all spells");
        minion = new Minion("giv", 450, 4, 5, 7, AttackType.RANGED, 5, spell,
                ActivationTime.ON_DEFEND);
        minion.setImmuneToAllSpells(true);
        minion.setFileName("f1_tier2general");
        cards.add(minion);


        buff = new AttackBuff(1, false, TargetType.MINION, SideType.ENEMY,
                RangeType.ONE, 16);
        buff.setRandom(true);
        spell = new Spell("sp", 0, 0, "decreases one random enemy minions health 16 " +
                "points");
        minion = new Minion("bahman", 300, 2, 6, 4, AttackType.RANGED, 7, spell,
                ActivationTime.ON_SPAWN);
        minion.setFileName("boss_borealjuggernaut");
        cards.add(minion);

        spell = new Spell("sp", 0, 0, "this minion cant be attacked by minions with " +
                "smaller AP");
        minion = new Minion("ashkboos", 400, 7, 14, 8, AttackType.MELEE, 0, spell,
                ActivationTime.ON_DEFEND);
        minion.setCanBeAttackedBySmallerMinions(false);
        minion.setFileName("f5_grandmasterkraigon");
        cards.add(minion);


        minion = new Minion("iraj", 500, 4, 6, 20, AttackType.RANGED, 3, null, null);
        minion.setFileName("boss_wraith");
        cards.add(minion);


        minion = new Minion("big-giant", 600, 9, 30, 8, AttackType.HYBRID, 2, null, null);
        minion.setFileName("boss_cindera");
        cards.add(minion);


        minion = new Minion("double-headed-giant", 550, 4, 10, 4, AttackType.MELEE, 0,
                null, null);
        minion.setFileName("f5_3rdgeneral");
        cards.add(minion);


        buff = new StunBuff(1, false, TargetType.MINION, SideType.ENEMY,
                RangeType.AROUND8);
        spell = new Spell("sp", 0, 0, "in 8 cell radius enemy minions will be stunned " +
                "for 1 round", buff);
        minion = new Minion("nane-sarma", 300, 2, 6, 4, AttackType.RANGED, 5, spell,
                ActivationTime.ON_SPAWN);
        minion.setFileName("boss_gol");
        cards.add(minion);


        buff = new HolyBuff(-1, true, TargetType.MINION, SideType.ALLY, RangeType.SELF,
                12);
        spell = new Spell("sp", 0, 0, "it has 12 holy buff continues", buff);
        minion = new Minion("steel-shield", 650, 3, 1, 1, AttackType.MELEE, 0, spell,
                ActivationTime.PASSIVE);
        minion.setFileName("f5_megabrontodon");
        cards.add(minion);


        buff = new AttackBuff(1, false, TargetType.HERO, SideType.ENEMY, RangeType.ONE,
                6);
        spell = new Spell("sp", 0, 0, "attacks enemy hero 6 points", buff);
        minion = new Minion("siavash", 350, 4, 8, 5, AttackType.MELEE, 0, spell,
                ActivationTime.ON_DEATH);
        minion.setFileName("f3_grandmasternoshrak");
        cards.add(minion);


        minion = new Minion("giant-king", 600, 5, 10, 4, AttackType.MELEE, 0, null,
                ActivationTime.COMBO);
        minion.setFileName("boss_skyfalltyrant");
        cards.add(minion);


        minion = new Minion("beast-arjang", 600, 3, 6, 6, AttackType.MELEE, 0, null,
                ActivationTime.COMBO);
        minion.setFileName("f4_altgeneraltier2");
        cards.add(minion);


//        heroes
        buff = new PowerBuff(-1, true, TargetType.HERO, SideType.ALLY, RangeType.SELF,
                EffectType.ATTACK_POWER, 4);
        spell = new Spell("sp", 0, 0, "increases his ap by 4", buff);
        Hero hero = new Hero("white-beast", 8000, 50, 4, AttackType.MELEE, 0, spell, 1,
                2);
        hero.setFileName("f6_tier2general");
        cards.add(hero);


        buff = new StunBuff(1, false, TargetType.MINION, SideType.ENEMY,
                RangeType.ALL_BOARD);
        spell = new Spell("sp", 0, 0, "stuns all enemy minions for 1 round", buff);
        hero = new Hero("phoenix", 9000, 50, 4, AttackType.MELEE, 0, spell, 5, 8);
        hero.setFileName("boss_legion");
        cards.add(hero);


        buff = new DisarmBuff(-1, false, TargetType.HERO_MINION, SideType.ENEMY,
                RangeType.ONE);
        spell = new Spell("sp", 0, 0, "disarms 1 enemy", buff);
        hero = new Hero("seven-headed-dragon", 8000, 50, 4, AttackType.MELEE, 0, spell,
                0, 1);
        hero.setFileName("neutral_boulderbreach");
        cards.add(hero);


        buff = new StunBuff(1, false, TargetType.MINION, SideType.ENEMY, RangeType.ONE);
        spell = new Spell("sp", 0, 0, "stuns one enemy minion for 1 round", buff);
        hero = new Hero("rakhsh", 8000, 50, 4, AttackType.MELEE, 0, spell, 0, 1);
        hero.setFileName("f5_ankylos");
        cards.add(hero);


        buff = new PoisonBuff(3, false, TargetType.HERO_MINION, SideType.ENEMY,
                RangeType.ONE);
        spell = new Spell("sp", 0, 0, "poisons enemy for 3 round on attack", buff);
        hero = new Hero("zahhak", 10000, 50, 2, AttackType.MELEE, 0, spell, 0, 0);
        hero.setOnAttack(true);
        hero.setFileName("f3_allomancer");
        cards.add(hero);


        buff = new HolyBuff(1, false, TargetType.HERO_MINION, SideType.ALLY,
                RangeType.ONE, 1);
        buff1 = new CellBuff(3, false, TargetType.CELL, SideType.ALLY,
                RangeType.SQUARE1, buff);
        spell = new Spell("sp", 0, 0, "makes a cell holy for 3 rounds", buff1);
        hero = new Hero("kaveh", 8000, 50, 4, AttackType.MELEE, 0, spell, 1, 3);
        hero.setFileName("f4_phantasm");
        cards.add(hero);


        buff = new AttackBuff(1, false, TargetType.MINION, SideType.ENEMY,
                RangeType.ALL_IN_ONE_ROW, 4);
        spell = new Spell("sp", 0, 0, "attacks all enemy minions in 1 row for 4 hp",
                buff);
        hero = new Hero("arash", 10000, 30, 2, AttackType.RANGED, 6, spell, 2, 2);
        hero.setFileName("neutral_astralprime");
        cards.add(hero);


        buff = new DispelBuff(1, false, TargetType.MINION, SideType.ENEMY, RangeType.ONE);
        spell = new Spell("sp", 0, 0, "dispels one enemy minion", buff);
        hero = new Hero("afsaneh", 11000, 40, 3, AttackType.RANGED, 3, spell, 1, 2);
        hero.setFileName("f5_upgradizon");
        cards.add(hero);


        buff = new HolyBuff(-1, true, TargetType.HERO, SideType.ALLY, RangeType.SELF, 3);
        spell = new Spell("sp", 0, 0, "adds a number 3 holy buff to himself continues",
                buff);
        hero = new Hero("esfandiar", 12000, 35, 3, AttackType.HYBRID, 3, spell, 0, 0);
        hero.setPassive(true);
        hero.setFileName("neutral_pennyarcade03");
        cards.add(hero);

        hero = new Hero("rostam", 8000, 55, 7, AttackType.HYBRID, 4, null, 0, 0);
        hero.setFileName("f6_greatwhitenorth");
        cards.add(hero);


//        items
        buff = new ManaBuff(3, false, null, SideType.ALLY, RangeType.ALL_BOARD, 1);
        buff.setCancelable(false);
        Item item = new UsableItem("wisdom-throne", 300, "increases mana by 1 for 3 " +
                "rounds",
                ActivationTime.ON_TURN, buff);
        items.add(item);


        buff = new HolyBuff(-1, false, TargetType.HERO, SideType.ALLY, RangeType.ONE, 12);
        item = new UsableItem("super-shield", 4000, "activates 12 holy buff in hero",
                ActivationTime.PASSIVE, buff);
        items.add(item);


        buff = new DisarmBuff(1, false, TargetType.MINION, SideType.ENEMY, RangeType.ONE);
        buff.setCancelable(false);
        buff.setAllyType(TargetType.HERO);
        buff.setAllyAttackTypes(AttackType.RANGED, AttackType.HYBRID);
        item = new UsableItem("damol-bow", 30000, "disarms enemy minion on hero range " +
                "or hybrid attack",
                ActivationTime.ON_ATTACK, buff);
        items.add(item);


        buff = new PowerBuff(-1, false, TargetType.MINION, SideType.ALLY, RangeType.ONE,
                EffectType.HEALTH, 6);
        buff.setCancelable(false);
        item = new CollectableItem("potion", "increases minion hp by 6",
                ActivationTime.PASSIVE, buff);
        items.add(item);


        buff = new PowerBuff(-1, false, TargetType.MINION, SideType.ALLY, RangeType.ONE
                , EffectType.ATTACK_POWER, 2);
        buff.setCancelable(false);
        buff.setAllyAttackTypes(AttackType.RANGED, AttackType.HYBRID);
        item = new CollectableItem("2-headed-arrow", "increases minion with " +
                "range/hybrid attack type ap by 2",
                ActivationTime.PASSIVE, buff);
        items.add(item);


        buff = new WeaknessBuff(-1, false, TargetType.HERO, SideType.ENEMY,
                RangeType.ONE, EffectType.ATTACK_POWER, 2);
        buff.setEnemyAttackTypes(AttackType.HYBRID, AttackType.RANGED);
        buff.setCancelable(false);
        item = new UsableItem("phoenix-feather", 3500, "weakens enemy hero with " +
                "range/hybrid attack type ap by 2 ",
                ActivationTime.PASSIVE, buff);
        items.add(item);


        buff = new PowerBuff(-1, false, TargetType.MINION, SideType.ALLY, RangeType.ONE
                , EffectType.HEALTH, 3);
        buff.setCancelable(false);
        buff1 = new PowerBuff(-1, false, TargetType.MINION, SideType.ALLY,
                RangeType.ONE, EffectType.ATTACK_POWER, 3);
        item = new CollectableItem("elixir", "increases ap and hp of minion by 3",
                ActivationTime.PASSIVE, buff, buff1);
        items.add(item);


        buff = new ManaBuff(1, false, null, SideType.ALLY, RangeType.ALL_BOARD, 3);
        buff.setCancelable(false);
        item = new CollectableItem("mana-potion", "increases mana by 3 in next round",
                ActivationTime.ON_TURN, buff);
        items.add(item);


        buff = new HolyBuff(2, false, TargetType.MINION, SideType.ALLY, RangeType.ONE,
                10);
        item = new CollectableItem("invincible-potion", "adds 10 holy buff to minion " +
                "for 2 rounds",
                ActivationTime.PASSIVE, buff);
        items.add(item);


        buff = new AttackBuff(1, false, TargetType.HERO_MINION, SideType.ENEMY,
                RangeType.ONE, 8);
        buff.setCancelable(false);
        buff.setRandom(true);
        item = new CollectableItem("death-curse", "minion will get an attack buff 8 on " +
                "death",
                ActivationTime.ON_DEATH, buff);
        items.add(item);


        buff = new PowerBuff(-1, false, TargetType.MINION, SideType.ALLY, RangeType.ONE
                , EffectType.ATTACK_POWER, 2);
        buff.setCancelable(false);
        item = new CollectableItem("random-damage", "gives minion 2 ap boost",
                ActivationTime.PASSIVE, buff);
        items.add(item);


        buff = new WeaknessBuff(1, false, TargetType.MINION, SideType.ENEMY,
                RangeType.ONE, EffectType.ATTACK_POWER, 2);
        buff.setRandom(true);
        item = new UsableItem("terror-hood", 5000, "weakens enemy ap by 2 for 1 round " +
                "on attack",
                ActivationTime.ON_ATTACK, buff);
        items.add(item);


        buff = new PowerBuff(-1, false, TargetType.MINION, SideType.ALLY, RangeType.ONE
                , EffectType.ATTACK_POWER, 6);
        buff.setCancelable(false);
        item = new CollectableItem("blades-of-agility", "boosts minion ap by 6",
                ActivationTime.PASSIVE, buff);
        items.add(item);


        buff = new ManaBuff(Integer.MAX_VALUE, false, null, SideType.ALLY,
                RangeType.ALL_BOARD, 1);
        item = new UsableItem("king-wisdom", 9000, "increases mana 1 point every round",
                ActivationTime.PASSIVE, buff);
        items.add(item);


        buff = new AttackBuff(1, false, TargetType.HERO, SideType.ENEMY, RangeType.ONE,
                1);
        buff.setCancelable(false);
        buff.setAllyType(TargetType.ALL_MINIONS);
        item = new UsableItem("assassination-dagger", 15000, "damages enemy hero on " +
                "minion spawn",
                ActivationTime.ON_SPAWN, buff);
        items.add(item);


        buff = new PoisonBuff(1, false, TargetType.MINION, SideType.ENEMY, RangeType.ONE);
        buff.setRandom(true);
        buff.setAllyType(TargetType.ALL_MINIONS);
        item = new UsableItem("poisonous-dagger", 7000, "poisons one random enemy " +
                "minion on attack",
                ActivationTime.ON_ATTACK, buff);
        items.add(item);


        buff = new DisarmBuff(1, false, TargetType.MINION, SideType.ENEMY, RangeType.ONE);
        buff.setCancelable(false);
        buff.setAllyType(TargetType.HERO);
        item = new UsableItem("shock-hammer", 15000, "hero disarms enemy minion for 1 " +
                "round on attack",
                ActivationTime.ON_ATTACK, buff);
        items.add(item);


        buff = new PowerBuff(-1, false, TargetType.MINION, SideType.ALLY, RangeType.ONE
                , EffectType.ATTACK_POWER, 1);
        buff.setAllyType(TargetType.ALL_MINIONS);
        buff.setRandom(true);
        item = new UsableItem("soul-eater", 25000, "all minions boost 1 random minion " +
                "ap on death",
                ActivationTime.ON_DEATH, buff);
        items.add(item);


        buff = new HolyBuff(2, false, TargetType.MINION, SideType.ALLY, RangeType.SELF,
                1);
        buff.setAllyType(TargetType.ALL_MINIONS);
        item = new UsableItem("baptise", 20000, "every minion will get a holy buff for " +
                "2 rounds on spawn",
                ActivationTime.ON_SPAWN, buff);
        items.add(item);


        buff = new PowerBuff(-1, false, TargetType.MINION, SideType.ALLY,
                RangeType.ALL_BOARD,
                EffectType.ATTACK_POWER, 5);
        buff.setCancelable(false);
        buff.setAllyAttackTypes(AttackType.MELEE);
        item = new CollectableItem("chinese-swordsman", "boosts ally minions with melee" +
                " attack by 5",
                ActivationTime.PASSIVE, buff);
        items.add(item);


        shop = new Shop(cards, items);
    }

    public static void add(Account account) {
        accounts.add(account);
    }

    public static boolean hasAccount(String userName) {
        return getAccountByUsername(userName) != null;
    }

    public static Account getAccountByUsername(String userName) {
        return accounts.stream().filter(account -> userName.equals(account.getUserName()))
                .findFirst().orElse(null);
    }

    public static List<Account> getSortedAccounts() {
        return accounts.stream()
                .sorted(Comparator
                        .comparing(Account::getWins).reversed()
                        .thenComparing(Account::getUserName))
                .collect(Collectors.toList());
    }

    public static Shop getShop() {
        return shop;
    }

    public static List<Account> getAccounts() {
        return accounts;
    }

    public static List<int[]> getRandomCoordinates(int count) {
        Collections.shuffle(nums);
        return nums.subList(0, count);
    }

    public static String getPath(String name) {
        try {
            return Files.walk(Paths.get("resources"))
                    .filter(path -> !Files.isDirectory(path))
                    .filter(path -> name.equals(path.getFileName().toString()))
                    .findFirst().orElseThrow(() -> new IOException("file not found"))
                    .toUri().toString();
        } catch (IOException e) {
            e.printStackTrace();
            return "";
        }
    }

    public static URI getURI(String name) throws URISyntaxException {
        return new URI(getPath(name));
    }

}
