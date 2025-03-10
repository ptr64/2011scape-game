package gg.rsmod.game.model.skill

import kotlin.math.floor
import kotlin.math.max
import kotlin.math.min
import kotlin.math.pow

/**
 * Holds all [Skill] data for a player.
 *
 * @author Tom <rspsmods@gmail.com>
 */
class SkillSet(val maxSkills: Int) {

    private val skills = Array(maxSkills) { index -> Skill(index) }

    /**
     * A flag which indicates if the skill's level and xp need to be sent to
     * the client on the next cycle.
     */
    var dirty = BooleanArray(maxSkills) { true }

    /**
     * Sum up the max level for all your skills.
     */
    val calculateTotalLevel: Int get() = skills.sumOf { skill -> getMaxLevel(skill.id) }

    /**
     * Sum up the experience for all your skills.
     */
    val calculateTotalXp: Double get() = skills.sumOf { skill -> getCurrentXp(skill.id) }

    /**
     * Get the [Skill] in [skills] with [skill] as its index
     */
    operator fun get(skill: Int): Skill = skills[skill]

    /**
     * If the [skill] data needs to be sent to the client.
     */
    fun isDirty(skill: Int): Boolean = dirty[skill]

    /**
     * Reset the [dirty] flag on all skills.
     */
    fun clean(skill: Int) {
        dirty[skill] = false
    }

    fun getCurrentXp(skill: Int): Double = skills[skill].xp

    fun getCurrentLevel(skill: Int): Int = skills[skill].currentLevel

    /**
     * Gets the level based on the xp that [skills].get([skill]) has.
     */
    fun getMaxLevel(skill: Int): Int = getLevelForXp(skills[skill].xp)

    fun setXp(skill: Int, xp: Double) {
        get(skill).xp = xp
        dirty[skill] = true
    }

    /**
     * Sets the 'current'/temporary level of the [skill].
     */
    fun setCurrentLevel(skill: Int, level: Int) {
        get(skill).currentLevel = level
        dirty[skill] = true
    }

    /**
     * Gets the level the player had before they last checked
     * it in the skill guide/level up interface
     */
    fun getLastLevel(skill: Int) : Int {
        return get(skill).lastLevel
    }

    /**
     * Sets the last level the player had before they
     * checked the level up skill guide
     */
    fun setLastLevel(skill: Int, level: Int) {
        get(skill).lastLevel = level
        dirty[skill] = true
    }

    /**
     * Sets the base, or real, level of the skill.
     */
    fun setBaseLevel(skill: Int, level: Int) {
        setBaseXp(skill, getXpForLevel(level))
    }

    /**
     * Sets the xp of the skill while also setting the current level
     * to the level respective to [xp].
     */
    fun setBaseXp(skill: Int, xp: Double) {
        setXp(skill, xp)
        setCurrentLevel(skill, getLevelForXp(xp))
    }

    /**
     * Alters the current level of the skill by adding [value] onto it.
     *
     * @param skill the skill level to alter.
     *
     * @param value the value which to add onto the current skill level. This
     * value can be negative.
     *
     * @param capValue the amount of levels which can be surpass the max level
     * in the skill. For example, if this value is set to [3] on a skill that
     * has is [99], that means that the level can be altered from [99] to [102].
     */
    fun alterCurrentLevel(skill: Int, value: Int, capValue: Int = 0) {
        check(capValue == 0 || capValue < 0 && value < 0 || capValue > 0 && value >= 0) {
            "Cap value and alter value must always be the same signum (+ or -)."
        }
        val altered = when {
            capValue > 0 -> min(getCurrentLevel(skill) + value, getMaxLevel(skill) + capValue)
            capValue < 0 -> max(getCurrentLevel(skill) + value, getMaxLevel(skill) + capValue)
            else -> min(getMaxLevel(skill), getCurrentLevel(skill) + value)
        }
        val newLevel = max(0, altered)
        val curLevel = getCurrentLevel(skill)

        if (newLevel != curLevel) {
            setCurrentLevel(skill = skill, level = newLevel)
        }
    }

    /**
     * Decrease the level of [skill].
     *
     * @param skill the skill level to alter.
     *
     * @param value the amount of levels which to decrease from [skill], as a
     * positive number.
     *
     * @param capped if true, the [skill] level cannot decrease further than
     * [getMaxLevel] - [value].
     */
    fun decrementCurrentLevel(skill: Int, value: Int, capped: Boolean) = alterCurrentLevel(skill, -value, if (capped) -value else 0)

    /**
     * Increase the level of [skill].
     *
     * @param skill the skill level to alter.
     *
     * @param value the amount of levels which to increase from [skill], as a
     * positive number.
     *
     * @param capped if true, the [skill] level cannot increase further than
     * [getMaxLevel].
     */
    fun incrementCurrentLevel(skill: Int, value: Int, capped: Boolean) = alterCurrentLevel(skill, value, if (capped) 0 else value)

    /**
     * Set [skill] level to [getMaxLevel].
     */
    fun restore(skill: Int) {
        setCurrentLevel(skill, getMaxLevel(skill))
    }

    /**
     * Restore all skill levels back to normal.
     */
    fun restoreAll() {
        skills.forEach { skill ->
            restore(skill.id)
        }
    }

    companion object {

        /**
         * The maximum amount of xp that can be set on a skill.
         */
        const val MAX_XP = 200_000_000

        /**
         * The maximum level a skill can reach.
         */
        const val MAX_LVL = 99

        /**
         * The default amount of trainable skills by players.
         */
        const val DEFAULT_SKILL_COUNT = 25

        /**
         * Gets the level correspondent to the [xp] given.
         */
        fun getLevelForXp(xp: Double): Int {
            for (guess in 1 until XP_TABLE.size) {
                if (xp < XP_TABLE[guess]) {
                    return guess
                }
            }
            return MAX_LVL
        }

        /**
         * Gets the xp you need to achieve to first reach [level].
         */
        fun getXpForLevel(level: Int): Double = XP_TABLE[level - 1].toDouble()

        /**
         * A table of the amount of xp needed to achieve 99 levels in a skill.
         */
        private val XP_TABLE = IntArray(99).apply {
            var points = 0
            for (level in 1 until size) {
                points += floor(level + 300 * 2.0.pow(level / 7.0)).toInt()
                set(level, points / 4)
            }
        }
    }
}