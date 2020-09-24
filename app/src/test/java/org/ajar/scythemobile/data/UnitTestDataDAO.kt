package org.ajar.scythemobile.data

class UnitTestDataDAO : UnitDataDAO {
    private val unitData = ArrayList<UnitData>()

    override fun getUnits(): List<UnitData>? {
        return unitData
    }

    override fun getUnit(id: Int): UnitData? {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getUnitsForPlayer(owner: Int, type: Int): List<UnitData>? {
        return unitData.filter { it.type == type && it.owner == owner }
    }

    override fun getUnitsAtLocation(loc: Int): List<UnitData>? {
        return unitData.filter { it.loc == loc}
    }

    override fun getSpecificUnitsAtLoc(loc: Int, owner: Int, types: List<Int>): List<UnitData>? {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getUnitsFromList(ids: List<Int>): List<UnitData>? {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun addUnit(vararg unit: UnitData) {
        unit.forEach { newUnit -> if(newUnit.id <= 0) newUnit.id = this.unitData.size; this.unitData.add(newUnit) }
    }

    override fun removeUnit(vararg unit: UnitData) {
        unit.forEach { remove -> this.unitData.removeIf { it.id == remove.id } }
    }

    override fun updateUnit(vararg unit: UnitData) {
        unit.forEach { update -> this.unitData.removeIf { it.id == update.id }; this.unitData.add(update) }
    }
}