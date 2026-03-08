package com.junhsiun.sit

data class SimpleSitConfig(
    val enabled: Boolean = true,
    val requireSneakRightClick: Boolean = false,
    val requireEmptyHand: Boolean = true,
    val allowStairs: Boolean = true,
    val allowCarpets: Boolean = true,
    val allowSlabs: Boolean = true,
    val allowBeds: Boolean = true,
    val allowCommandSit: Boolean = true
)
