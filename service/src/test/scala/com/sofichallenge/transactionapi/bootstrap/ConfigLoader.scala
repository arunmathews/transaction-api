package com.sofichallenge.transactionapi.bootstrap

import java.io.File

import com.typesafe.config.ConfigFactory

/**
  *
  */
trait ConfigLoader {
  private val baseConf = ConfigFactory.load()
  private val overrideConf =  ConfigFactory.parseFile(new File(TestMagicConstants.testConfigLocation))
  val conf = ConfigFactory.load(overrideConf).withFallback(baseConf)

}
