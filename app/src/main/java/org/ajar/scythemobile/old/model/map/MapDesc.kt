package org.ajar.scythemobile.old.model.map

import org.ajar.scythemobile.old.model.faction.FactionMat

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

class MapDesc(vararg val mapHexDescs: MapHexDesc =
    arrayOf(
        MapHexDesc(1, HexNeighbors(sw = 8, se = 9), HomeBase(FactionMat.ALBION)),
        MapHexDesc(2, HexNeighbors(sw = 11, se = 12), HomeBase(FactionMat.NORDIC)),
        MapHexDesc(3, HexNeighbors(e = 21, se = 27), HomeBase(FactionMat.POLONIA)),
        MapHexDesc(4, HexNeighbors(w = 26, sw = 33), HomeBase(FactionMat.RUSVIET), RiverFeature(direction = Direction.NW)),
        MapHexDesc(5, HexNeighbors(ne = 41, e = 48), HomeBase(FactionMat.SAXONY)),
        MapHexDesc(6, HexNeighbors(w = 53, nw = 47), HomeBase(FactionMat.TOGAWA)),
        MapHexDesc(7, HexNeighbors(ne = 50, e = 54), HomeBase(FactionMat.CRIMEA)),

        MapHexDesc(8, HexNeighbors(sw = 14, se = 15, e = 9), ResourceFeature.MOUNTAIN),
        MapHexDesc(9, HexNeighbors(w = 8, sw = 15, se = 16, e = 10), ResourceFeature.FARM),
        MapHexDesc(10, HexNeighbors(w = 9, sw = 16, se = 17, e = 11), ResourceFeature.VILLAGE, SpecialFeature.ENCOUNTER, RiverFeature(direction = Direction.E), RiverFeature(direction = Direction.SE)),
        MapHexDesc(11, HexNeighbors(w = 10, sw = 17, se = 18, e = 12), ResourceFeature.FOREST, RiverFeature(direction = Direction.W), RiverFeature(direction = Direction.SW)),
        MapHexDesc(12, HexNeighbors(w = 11, sw = 18, se = 19, e = 13), ResourceFeature.TUNDRA, RiverFeature(direction = Direction.E), RiverFeature(direction = Direction.SE)),
        MapHexDesc(13, HexNeighbors(w = 12, sw = 19, se = 20), ResourceFeature.VILLAGE, RiverFeature(direction = Direction.W)),

        MapHexDesc(14, HexNeighbors(ne = 8, e = 15, se = 21), SpecialFeature.LAKE),
        MapHexDesc(15, HexNeighbors(w = 14, nw = 8, ne = 9, e = 16, se = 22, sw = 21), ResourceFeature.TUNDRA, SpecialFeature.ENCOUNTER, RiverFeature(direction = Direction.SW)),
        MapHexDesc(16, HexNeighbors(w = 15, nw = 9, ne = 10, e = 17, se = 23, sw = 22), SpecialFeature.LAKE),
        MapHexDesc(17, HexNeighbors(w = 16, nw = 10, ne = 11, e = 18, se = 24, sw = 23), ResourceFeature.TUNDRA, SpecialFeature.TUNNEL, RiverFeature(direction = Direction.NW), RiverFeature(direction = Direction.NE), RiverFeature(direction = Direction.E)),
        MapHexDesc(18, HexNeighbors(w = 17, nw = 11, ne = 12, e = 19, se = 25, sw = 24), ResourceFeature.MOUNTAIN, SpecialFeature.ENCOUNTER, RiverFeature(direction = Direction.E), RiverFeature(direction = Direction.W), RiverFeature(direction = Direction.SE)),
        MapHexDesc(19, HexNeighbors(w = 18, nw = 12, ne = 13, e = 20, se = 26, sw = 25), ResourceFeature.FARM, RiverFeature(direction = Direction.NW), RiverFeature(direction = Direction.SE), RiverFeature(direction = Direction.W)),
        MapHexDesc(20, HexNeighbors(w = 19, nw = 13, sw = 26), ResourceFeature.FARM, SpecialFeature.ENCOUNTER, RiverFeature(direction = Direction.SE), RiverFeature(direction = Direction.SW)),

        MapHexDesc(21, HexNeighbors(nw = 14, ne = 15, e = 22, se = 28, sw = 27), ResourceFeature.FOREST, RiverFeature(direction = Direction.E), RiverFeature(direction = Direction.NE)),
        MapHexDesc(22, HexNeighbors(w = 21, nw = 15, ne = 16, e = 23, se = 29, sw = 28), ResourceFeature.MOUNTAIN, SpecialFeature.TUNNEL, RiverFeature(direction = Direction.SW), RiverFeature(direction = Direction.W)),
        MapHexDesc(23, HexNeighbors(w = 22, nw = 16, ne = 17, e = 24, se = 30, sw = 29), ResourceFeature.FOREST),
        MapHexDesc(24, HexNeighbors(w = 23, nw = 17, ne = 18, e = 25, se = 31, sw = 30), SpecialFeature.LAKE),
        MapHexDesc(25, HexNeighbors(w = 24, nw = 18, ne = 19, e = 26, se = 32, sw = 31), ResourceFeature.FOREST, SpecialFeature.TUNNEL, RiverFeature(direction = Direction.NW), RiverFeature(direction = Direction.SE), RiverFeature(direction = Direction.E)),
        MapHexDesc(26, HexNeighbors(w = 25, nw = 19, ne = 20, se = 33, sw = 32), ResourceFeature.FOREST, SpecialFeature.TUNNEL, RiverFeature(direction = Direction.W), RiverFeature(direction = Direction.NE), RiverFeature(direction = Direction.NW)),

        MapHexDesc(27, HexNeighbors(ne = 21, e = 28, se = 35, sw = 34), ResourceFeature.FARM, RiverFeature(direction = Direction.SE), RiverFeature(direction = Direction.SW)),
        MapHexDesc(28, HexNeighbors(w = 27, nw = 21, ne = 22, e = 29, se = 36, sw = 35), ResourceFeature.VILLAGE, SpecialFeature.ENCOUNTER, RiverFeature(direction = Direction.SE), RiverFeature(direction = Direction.SW), RiverFeature(direction = Direction.NE)),
        MapHexDesc(29, HexNeighbors(w = 28, nw = 22, ne = 23, e = 30, se = 37, sw = 36), SpecialFeature.LAKE),
        MapHexDesc(30, HexNeighbors(w = 29, nw = 23, ne = 24, e = 31, se = 38, sw = 37), SpecialFeature.FACTORY),
        MapHexDesc(31, HexNeighbors(w = 30, nw = 24, ne = 25, e = 32, se = 39, sw = 38), ResourceFeature.MOUNTAIN, RiverFeature(direction = Direction.E)),
        MapHexDesc(32, HexNeighbors(w = 31, nw = 25, ne = 26, e = 33, se = 40, sw = 39), ResourceFeature.TUNDRA, SpecialFeature.ENCOUNTER, RiverFeature(direction = Direction.NW), RiverFeature(direction = Direction.SW), RiverFeature(direction = Direction.W)),
        MapHexDesc(33, HexNeighbors(w = 32, nw = 26, sw = 40), ResourceFeature.MOUNTAIN),

        MapHexDesc(34, HexNeighbors(ne = 27, e = 35, se = 41), ResourceFeature.FOREST, SpecialFeature.ENCOUNTER, RiverFeature(direction = Direction.NE), RiverFeature(direction = Direction.SW), RiverFeature(direction = Direction.SE)),
        MapHexDesc(35, HexNeighbors(w = 34, nw = 27, ne = 28, e = 36, se = 42, sw = 41), ResourceFeature.FOREST, RiverFeature(direction = Direction.NW), RiverFeature(direction = Direction.NE), RiverFeature(direction = Direction.SW), RiverFeature(direction = Direction.SE)),
        MapHexDesc(36, HexNeighbors(w = 35, nw = 28, ne = 29, e = 37, se = 43, sw = 42), ResourceFeature.FARM, SpecialFeature.TUNNEL, RiverFeature(direction = Direction.NW), RiverFeature(direction = Direction.SW)),
        MapHexDesc(37, HexNeighbors(w = 36, nw = 29, ne = 30, e = 38, se = 44, sw = 43), ResourceFeature.TUNDRA),
        MapHexDesc(38, HexNeighbors(w = 37, nw = 30, ne = 31, e = 39, se = 45, sw = 44), SpecialFeature.LAKE),
        MapHexDesc(39, HexNeighbors(w = 38, nw = 31, ne = 32, e = 40, se = 46, sw = 45), ResourceFeature.VILLAGE, SpecialFeature.TUNNEL, RiverFeature(direction = Direction.NE)),
        MapHexDesc(40, HexNeighbors(w = 39, nw = 32, ne = 33, se = 47, sw = 46), SpecialFeature.LAKE),

        MapHexDesc(41, HexNeighbors(nw = 34, ne = 35, e = 42, se = 48), ResourceFeature.MOUNTAIN, RiverFeature(direction = Direction.NW), RiverFeature(direction = Direction.NE)),
        MapHexDesc(42, HexNeighbors(w = 41, nw = 35, ne = 36, e = 43, se = 49, sw = 48), ResourceFeature.VILLAGE, SpecialFeature.ENCOUNTER, RiverFeature(direction = Direction.NW), RiverFeature(direction = Direction.NE), RiverFeature(direction = Direction.E)),
        MapHexDesc(43, HexNeighbors(w = 42, nw = 36, ne = 37, e = 44, se = 50, sw = 49), ResourceFeature.VILLAGE, SpecialFeature.ENCOUNTER, RiverFeature(direction = Direction.W), RiverFeature(direction = Direction.SE)),
        MapHexDesc(44, HexNeighbors(w = 43, nw = 37, ne = 38, e = 45, se = 51, sw = 50), ResourceFeature.TUNDRA, SpecialFeature.TUNNEL, RiverFeature(direction = Direction.SW), RiverFeature(direction = Direction.SE)),
        MapHexDesc(45, HexNeighbors(w = 44, nw = 38, ne = 39, e = 46, se = 52, sw = 51), ResourceFeature.VILLAGE, RiverFeature(direction = Direction.SW)),
        MapHexDesc(46, HexNeighbors(w = 45, nw = 39, ne = 40, e = 47, se = 53, sw = 52), ResourceFeature.MOUNTAIN, SpecialFeature.ENCOUNTER),
        MapHexDesc(47, HexNeighbors(w = 46, nw = 40, sw = 53), ResourceFeature.TUNDRA),

        MapHexDesc(48, HexNeighbors(nw = 41, ne = 42, e = 49), ResourceFeature.TUNDRA),
        MapHexDesc(49, HexNeighbors(w = 48, nw = 42, ne = 43, e = 50), SpecialFeature.LAKE),
        MapHexDesc(50, HexNeighbors(w = 49, nw = 43, ne = 44, e = 51, se = 54), ResourceFeature.FARM, RiverFeature(direction = Direction.NE), RiverFeature(direction = Direction.NW)),
        MapHexDesc(51, HexNeighbors(w = 50, nw = 44, ne = 45, e = 52, sw = 54), ResourceFeature.MOUNTAIN, SpecialFeature.ENCOUNTER, RiverFeature(direction = Direction.NE), RiverFeature(direction = Direction.NW), RiverFeature(direction = Direction.E)),
        MapHexDesc(52, HexNeighbors(w = 51, nw = 45, ne = 46, e = 53), ResourceFeature.VILLAGE, RiverFeature(direction = Direction.W)),
        MapHexDesc(53, HexNeighbors(w = 52, nw = 46, ne = 47), ResourceFeature.FARM),

        MapHexDesc(54, HexNeighbors(nw = 50, ne = 51), ResourceFeature.VILLAGE)
    )
)