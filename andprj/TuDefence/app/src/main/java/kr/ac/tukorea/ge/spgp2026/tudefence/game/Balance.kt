package kr.ac.tukorea.ge.spgp2026.tudefence.game

object Balance {
    const val INITIAL_GOLD = 30

    object Cannon {
        val costs = intArrayOf(
            10, 100, 300, 700, 1500, 3000, 7000, 15000, 40000, 100000, 100000000
        )
        const val upgradeCostRatio = 1.1f
        const val sellRatio = 0.5f

        const val minBarrelSize = 110f
        const val maxBarrelSize = 200f
        const val maxFireInterval = 5.0f
        const val fireIntervalStep = 0.4f
        const val baseRange = 100f
        const val rangePerLevel = 100f
    }

    object Shell {
        const val speed = 600f
        const val basePower = 10f
        const val powerRatio = 1.2f
        const val splashMinLevel = 6
        const val baseExplosionRadius = 60f
        const val explosionRadiusPowerRatio = 3f
    }

    object Fly {
        const val bossHealth = 150f
        const val redHealth = 50f
        const val blueHealth = 30f
        const val cyanHealth = 20f
        const val dragonHealth = 10f

        const val redSpawnRate = 10
        const val blueSpawnRate = 20
        const val cyanSpawnRate = 30

        const val minSize = 75f
        const val maxSize = 125f
        const val bossSizeScale = 1.5f
        const val minSpeed = 25f
        const val maxSpeed = 60f
    }

    object Wave {
        const val intervalInit = 2.0f
        const val intervalMin = 0.1f
        const val intervalDecay = 0.995f
        const val waveInterval = 30.0f
        const val timeScale = 3.0f
        const val speedRatioPerWave = 1.0f
    }
}
