package org.ajar.scythemobile.data

class MapHexTestDAO : MapHexDAO {
    private val mapHexData = ArrayList<MapHexData>()

    override fun getMap(): List<MapHexData>? {
        return mapHexData
    }

    override fun getMapHex(loc: Int): MapHexData? {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getTunnels(): List<MapHexData>? {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getFeatureHexs(feature: Int): List<MapHexData>? {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getHomeBase(vararg faction: Int): List<MapHexData>? {
        return mapHexData.filter { it.faction?.let { fac -> faction.contains(fac) }?: false }
    }

    override fun addMapHex(vararg hex: MapHexData) {
        hex.forEach { newHex -> this.mapHexData.add(newHex) }
    }

    override fun removeMapHex(vararg hex: MapHexData) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun updateMapHex(vararg hex: MapHexData) {
        hex.forEach { update -> this.mapHexData.removeIf { it.loc == update.loc }; this.mapHexData.add(update) }
    }
}