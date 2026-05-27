package kr.ac.tukorea.ge.spgp2026.tudefence.game

object Balance {
    const val INITIAL_GOLD = 60

    object Cannon {
        val costs = intArrayOf(
            20, 55, 110, 200, 340, 550, 850, 1250, 1800, 2500
        )
        const val upgradeCostRatio = 0.9f
        const val sellRatio = 0.6f

        const val minBarrelSize = 110f
        const val maxBarrelSize = 200f
        const val maxFireInterval = 3.0f
        const val fireIntervalStep = 0.18f
        const val baseRange = 140f
        const val rangePerLevel = 45f
    }

    object Shell {
        const val speed = 700f
        const val basePower = 9f
        const val powerRatio = 1.18f
        const val splashMinLevel = 6
        const val baseExplosionRadius = 45f
        const val explosionRadiusPowerRatio = 1.8f
    }

    object Fly {
        const val bossHealth = 260f
        const val redHealth = 70f
        const val blueHealth = 42f
        const val cyanHealth = 24f
        const val dragonHealth = 12f

        const val redSpawnRate = 8
        const val blueSpawnRate = 17
        const val cyanSpawnRate = 30

        const val minSize = 70f
        const val maxSize = 115f
        const val bossSizeScale = 1.4f
        const val minSpeed = 32f
        const val maxSpeed = 70f
    }

    object Wave {
        const val intervalInit = 1.6f
        const val intervalMin = 0.35f
        const val intervalDecay = 0.992f
        const val waveInterval = 35.0f
        const val timeScale = 1.0f
        const val speedRatioPerWave = 1.08f
    }
}
