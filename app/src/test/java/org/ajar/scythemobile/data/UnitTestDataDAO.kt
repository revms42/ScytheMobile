package org.ajar.scythemobile.data

class UnitTestDataDAO : UnitDataDAO {
    private val unitData = ArrayList<UnitData>()

    override fun getUnits(): List<UnitData>? {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getUnit(id: Int): UnitData? {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getUnitsForPlayer(owner: Int, type: Int): List<UnitData>? {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getUnitsAtLocation(loc: Int): List<UnitData>? {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getSpecificUnitsAtLoc(loc: Int, owner: Int, types: List<Int>): List<UnitData>? {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getUnitsFromList(ids: List<Int>): List<UnitData>? {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun addUnit(vararg unit: UnitData) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun removeUnit(vararg unit: UnitData) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun updateUnit(vararg unit: UnitData) {
        unit.forEach { update -> this.unitData.removeIf { it.id == update.id }; this.unitData.add(update) }
    }
}