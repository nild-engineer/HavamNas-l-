package com.example.hava1

import com.example.hava1.domain.history.RecentCityHistory
import com.example.hava1.model.*
import org.junit.Assert.*
import org.junit.Test

class RecentCityHistoryTest {
    private fun city(id: Int, name: String = "Şehir $id") =
        CitySearchResult(id, name, 40.0, 26.0, "Türkiye", null)

    @Test
    fun newCityIsFirst() {
        assertEquals(listOf(2, 1), RecentCityHistory.add(listOf(city(1)), city(2)).map { it.id })
    }

    @Test
    fun repeatedCityMovesToFrontWithoutDuplicate() {
        assertEquals(
            listOf(1, 2),
            RecentCityHistory.add(listOf(city(2), city(1)), city(1)).map { it.id },
        )
    }

    @Test
    fun historyHasMaximumSixCities() {
        val result =
            (1..9).fold(emptyList<CitySearchResult>()) { history, id ->
                RecentCityHistory.add(history, city(id))
            }
        assertEquals(listOf(9, 8, 7, 6, 5, 4), result.map { it.id })
    }

    @Test
    fun removalLeavesOtherCities() {
        assertEquals(
            listOf(1, 3),
            RecentCityHistory.remove(listOf(city(1), city(2), city(3)), 2).map { it.id },
        )
    }

    @Test
    fun sameNameDifferentIdsRemainDistinct() {
        assertEquals(
            2,
            RecentCityHistory.add(listOf(city(1, "Cambridge")), city(2, "Cambridge")).size,
        )
    }

    @Test
    fun removalDoesNotModifyOriginalList() {
        val original = listOf(city(1), city(2))
        RecentCityHistory.remove(original, 1)
        assertEquals(2, original.size)
    }
}
