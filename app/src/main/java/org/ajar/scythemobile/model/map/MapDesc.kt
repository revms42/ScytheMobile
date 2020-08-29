package org.ajar.scythemobile.model.map

import org.ajar.scythemobile.data.Neighbors
import org.ajar.scythemobile.model.faction.StandardFactionMat

/*
 * https://www.inkedgaming.com/products/scythe-neoprene-board-game-mat-36-x-28
 * Rivers: East = x01, West = x02, South East = x04, South West = x08, North East = x10, North West = x20
 * Encounter = E
 * Mountain  = M
 * Farm      = F
 * Tundra    = O
 * Lake      = L
 * Tunnel    = R
 * Town      = T
 * Forest    = W
 * Factory   = X
 *
 *    |     |*Alb*|     |     |*Nor*|     |     |
 * |     M_x00|F_x00|TEx05|W_x0A|O_x05|T_x02|     |     8,  9,  10, 11, 12, 13
 *    |L_x00|OEx08|L_x00|ORx31|MEx07|F_x26|FEx0C|     14, 15, 16, 17, 18, 19, 20
 * |*Pol*|W_x11|MRx0A|W_x00|L_x00|WRx25|T_x32|*Rus*|    21, 22, 23, 24, 25, 26
 *    |F_x0C|TEx1C|L_x00|X_x00|M_x01|OEx2A|M_x00|     27, 28, 29, 30, 31, 32, 33
 * |WEx1C|W_x3C|FRx28|O_x00|L_x00|TRx10|L_x00|     |34, 35, 36, 37, 38, 39, 40
 *    |M_x30|TEx31|TEx06|ORx0C|T_x08|MEx00|O_x00|     41, 42, 43, 44, 45, 46, 47
 * |*Sax*|O_x00|L_x00|F_x30|MEx31|T_x02|F_x00|*Tok*|    48, 49, 50, 51, 52, 53
 *    |     |     |*Cri*|T_x00|     |     |     |                 54
 */

class MapDesc(vararg val mapHexDesc: MapHexDesc =
    arrayOf(
        MapHexDesc(1, Neighbors(sw = 8, se = 9), HomeBase(StandardFactionMat.ALBION)),
        MapHexDesc(2, Neighbors(sw = 11, se = 12), HomeBase(StandardFactionMat.NORDIC)),
        MapHexDesc(3, Neighbors(e = 21, se = 27), HomeBase(StandardFactionMat.POLONIA)),
        MapHexDesc(4, Neighbors(w = 26, sw = 33), HomeBase(StandardFactionMat.RUSVIET), RiverFeature(direction = Direction.NW)),
        MapHexDesc(5, Neighbors(ne = 41, e = 48), HomeBase(StandardFactionMat.SAXONY)),
        MapHexDesc(6, Neighbors(w = 53, nw = 47), HomeBase(StandardFactionMat.TOGAWA)),
        MapHexDesc(7, Neighbors(ne = 50, e = 54), HomeBase(StandardFactionMat.CRIMEA)),

        MapHexDesc(8, Neighbors(sw = 14, se = 15, e = 9), TerrainFeature.MOUNTAIN),
        MapHexDesc(9, Neighbors(w = 8, sw = 15, se = 16, e = 10), TerrainFeature.FIELD),
        MapHexDesc(10, Neighbors(w = 9, sw = 16, se = 17, e = 11), TerrainFeature.VILLAGE, SpecialFeature.ENCOUNTER, RiverFeature(direction = Direction.E), RiverFeature(direction = Direction.SE)),
        MapHexDesc(11, Neighbors(w = 10, sw = 17, se = 18, e = 12), TerrainFeature.FOREST, RiverFeature(direction = Direction.W), RiverFeature(direction = Direction.SW)),
        MapHexDesc(12, Neighbors(w = 11, sw = 18, se = 19, e = 13), TerrainFeature.TUNDRA, RiverFeature(direction = Direction.E), RiverFeature(direction = Direction.SE)),
        MapHexDesc(13, Neighbors(w = 12, sw = 19, se = 20), TerrainFeature.VILLAGE, RiverFeature(direction = Direction.W)),

        MapHexDesc(14, Neighbors(ne = 8, e = 15, se = 21), TerrainFeature.LAKE),
        MapHexDesc(15, Neighbors(w = 14, nw = 8, ne = 9, e = 16, se = 22, sw = 21), TerrainFeature.TUNDRA, SpecialFeature.ENCOUNTER, RiverFeature(direction = Direction.SW)),
        MapHexDesc(16, Neighbors(w = 15, nw = 9, ne = 10, e = 17, se = 23, sw = 22), TerrainFeature.LAKE),
        MapHexDesc(17, Neighbors(w = 16, nw = 10, ne = 11, e = 18, se = 24, sw = 23), TerrainFeature.TUNDRA, SpecialFeature.TUNNEL, RiverFeature(direction = Direction.NW), RiverFeature(direction = Direction.NE), RiverFeature(direction = Direction.E)),
        MapHexDesc(18, Neighbors(w = 17, nw = 11, ne = 12, e = 19, se = 25, sw = 24), TerrainFeature.MOUNTAIN, SpecialFeature.ENCOUNTER, RiverFeature(direction = Direction.E), RiverFeature(direction = Direction.W), RiverFeature(direction = Direction.SE)),
        MapHexDesc(19, Neighbors(w = 18, nw = 12, ne = 13, e = 20, se = 26, sw = 25), TerrainFeature.FIELD, RiverFeature(direction = Direction.NW), RiverFeature(direction = Direction.SE), RiverFeature(direction = Direction.W)),
        MapHexDesc(20, Neighbors(w = 19, nw = 13, sw = 26), TerrainFeature.FIELD, SpecialFeature.ENCOUNTER, RiverFeature(direction = Direction.SE), RiverFeature(direction = Direction.SW)),

        MapHexDesc(21, Neighbors(nw = 14, ne = 15, e = 22, se = 28, sw = 27), TerrainFeature.FOREST, RiverFeature(direction = Direction.E), RiverFeature(direction = Direction.NE)),
        MapHexDesc(22, Neighbors(w = 21, nw = 15, ne = 16, e = 23, se = 29, sw = 28), TerrainFeature.MOUNTAIN, SpecialFeature.TUNNEL, RiverFeature(direction = Direction.SW), RiverFeature(direction = Direction.W)),
        MapHexDesc(23, Neighbors(w = 22, nw = 16, ne = 17, e = 24, se = 30, sw = 29), TerrainFeature.FOREST),
        MapHexDesc(24, Neighbors(w = 23, nw = 17, ne = 18, e = 25, se = 31, sw = 30), TerrainFeature.LAKE),
        MapHexDesc(25, Neighbors(w = 24, nw = 18, ne = 19, e = 26, se = 32, sw = 31), TerrainFeature.FOREST, SpecialFeature.TUNNEL, RiverFeature(direction = Direction.NW), RiverFeature(direction = Direction.SE), RiverFeature(direction = Direction.E)),
        MapHexDesc(26, Neighbors(w = 25, nw = 19, ne = 20, se = 33, sw = 32), TerrainFeature.FOREST, SpecialFeature.TUNNEL, RiverFeature(direction = Direction.W), RiverFeature(direction = Direction.NE), RiverFeature(direction = Direction.NW)),

        MapHexDesc(27, Neighbors(ne = 21, e = 28, se = 35, sw = 34), TerrainFeature.FIELD, RiverFeature(direction = Direction.SE), RiverFeature(direction = Direction.SW)),
        MapHexDesc(28, Neighbors(w = 27, nw = 21, ne = 22, e = 29, se = 36, sw = 35), TerrainFeature.VILLAGE, SpecialFeature.ENCOUNTER, RiverFeature(direction = Direction.SE), RiverFeature(direction = Direction.SW), RiverFeature(direction = Direction.NE)),
        MapHexDesc(29, Neighbors(w = 28, nw = 22, ne = 23, e = 30, se = 37, sw = 36), TerrainFeature.LAKE),
        MapHexDesc(30, Neighbors(w = 29, nw = 23, ne = 24, e = 31, se = 38, sw = 37), TerrainFeature.FACTORY),
        MapHexDesc(31, Neighbors(w = 30, nw = 24, ne = 25, e = 32, se = 39, sw = 38), TerrainFeature.MOUNTAIN, RiverFeature(direction = Direction.E)),
        MapHexDesc(32, Neighbors(w = 31, nw = 25, ne = 26, e = 33, se = 40, sw = 39), TerrainFeature.TUNDRA, SpecialFeature.ENCOUNTER, RiverFeature(direction = Direction.NW), RiverFeature(direction = Direction.SW), RiverFeature(direction = Direction.W)),
        MapHexDesc(33, Neighbors(w = 32, nw = 26, sw = 40), TerrainFeature.MOUNTAIN),

        MapHexDesc(34, Neighbors(ne = 27, e = 35, se = 41), TerrainFeature.FOREST, SpecialFeature.ENCOUNTER, RiverFeature(direction = Direction.NE), RiverFeature(direction = Direction.SW), RiverFeature(direction = Direction.SE)),
        MapHexDesc(35, Neighbors(w = 34, nw = 27, ne = 28, e = 36, se = 42, sw = 41), TerrainFeature.FOREST, RiverFeature(direction = Direction.NW), RiverFeature(direction = Direction.NE), RiverFeature(direction = Direction.SW), RiverFeature(direction = Direction.SE)),
        MapHexDesc(36, Neighbors(w = 35, nw = 28, ne = 29, e = 37, se = 43, sw = 42), TerrainFeature.FIELD, SpecialFeature.TUNNEL, RiverFeature(direction = Direction.NW), RiverFeature(direction = Direction.SW)),
        MapHexDesc(37, Neighbors(w = 36, nw = 29, ne = 30, e = 38, se = 44, sw = 43), TerrainFeature.TUNDRA),
        MapHexDesc(38, Neighbors(w = 37, nw = 30, ne = 31, e = 39, se = 45, sw = 44), TerrainFeature.LAKE),
        MapHexDesc(39, Neighbors(w = 38, nw = 31, ne = 32, e = 40, se = 46, sw = 45), TerrainFeature.VILLAGE, SpecialFeature.TUNNEL, RiverFeature(direction = Direction.NE)),
        MapHexDesc(40, Neighbors(w = 39, nw = 32, ne = 33, se = 47, sw = 46), TerrainFeature.LAKE),

        MapHexDesc(41, Neighbors(nw = 34, ne = 35, e = 42, se = 48), TerrainFeature.MOUNTAIN, RiverFeature(direction = Direction.NW), RiverFeature(direction = Direction.NE)),
        MapHexDesc(42, Neighbors(w = 41, nw = 35, ne = 36, e = 43, se = 49, sw = 48), TerrainFeature.VILLAGE, SpecialFeature.ENCOUNTER, RiverFeature(direction = Direction.NW), RiverFeature(direction = Direction.NE), RiverFeature(direction = Direction.E)),
        MapHexDesc(43, Neighbors(w = 42, nw = 36, ne = 37, e = 44, se = 50, sw = 49), TerrainFeature.VILLAGE, SpecialFeature.ENCOUNTER, RiverFeature(direction = Direction.W), RiverFeature(direction = Direction.SE)),
        MapHexDesc(44, Neighbors(w = 43, nw = 37, ne = 38, e = 45, se = 51, sw = 50), TerrainFeature.TUNDRA, SpecialFeature.TUNNEL, RiverFeature(direction = Direction.SW), RiverFeature(direction = Direction.SE)),
        MapHexDesc(45, Neighbors(w = 44, nw = 38, ne = 39, e = 46, se = 52, sw = 51), TerrainFeature.VILLAGE, RiverFeature(direction = Direction.SW)),
        MapHexDesc(46, Neighbors(w = 45, nw = 39, ne = 40, e = 47, se = 53, sw = 52), TerrainFeature.MOUNTAIN, SpecialFeature.ENCOUNTER),
        MapHexDesc(47, Neighbors(w = 46, nw = 40, sw = 53), TerrainFeature.TUNDRA),

        MapHexDesc(48, Neighbors(nw = 41, ne = 42, e = 49), TerrainFeature.TUNDRA),
        MapHexDesc(49, Neighbors(w = 48, nw = 42, ne = 43, e = 50), TerrainFeature.LAKE),
        MapHexDesc(50, Neighbors(w = 49, nw = 43, ne = 44, e = 51, se = 54), TerrainFeature.FIELD, RiverFeature(direction = Direction.NE), RiverFeature(direction = Direction.NW)),
        MapHexDesc(51, Neighbors(w = 50, nw = 44, ne = 45, e = 52, sw = 54), TerrainFeature.MOUNTAIN, SpecialFeature.ENCOUNTER, RiverFeature(direction = Direction.NE), RiverFeature(direction = Direction.NW), RiverFeature(direction = Direction.E)),
        MapHexDesc(52, Neighbors(w = 51, nw = 45, ne = 46, e = 53), TerrainFeature.VILLAGE, RiverFeature(direction = Direction.W)),
        MapHexDesc(53, Neighbors(w = 52, nw = 46, ne = 47), TerrainFeature.FIELD),

        MapHexDesc(54, Neighbors(nw = 50, ne = 51), TerrainFeature.VILLAGE)
    )
)