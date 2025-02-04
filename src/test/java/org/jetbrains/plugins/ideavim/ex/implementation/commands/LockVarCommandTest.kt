/*
 * Copyright 2003-2023 The IdeaVim authors
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE.txt file or at
 * https://opensource.org/licenses/MIT.
 */

package org.jetbrains.plugins.ideavim.ex.implementation.commands

import org.jetbrains.plugins.ideavim.SkipNeovimReason
import org.jetbrains.plugins.ideavim.TestWithoutNeovim
import org.jetbrains.plugins.ideavim.VimTestCase

class LockVarCommandTest : VimTestCase() {

  @TestWithoutNeovim(SkipNeovimReason.PLUGIN_ERROR)
  fun `test lock int variable`() {
    configureByText("\n")
    typeText(commandToKeys("let x = 10"))
    typeText(commandToKeys("lockvar x"))
    typeText(commandToKeys("let x = 15"))
    assertPluginError(true)
    assertPluginErrorMessageContains("E741: Value is locked: x")
  }

  fun `test unlock int variable`() {
    configureByText("\n")
    typeText(commandToKeys("let x = 10"))
    typeText(commandToKeys("lockvar x"))
    typeText(commandToKeys("unlockvar x"))
    typeText(commandToKeys("let x = 15"))
    assertPluginError(false)
    typeText(commandToKeys("echo x"))
    assertExOutput("15\n")
  }

  @TestWithoutNeovim(SkipNeovimReason.PLUGIN_ERROR)
  fun `test lock list variable`() {
    configureByText("\n")
    typeText(commandToKeys("let x = [1]"))
    typeText(commandToKeys("lockvar x"))
    typeText(commandToKeys("let x = 15"))
    assertPluginError(true)
    assertPluginErrorMessageContains("E741: Value is locked: x")
  }

  @TestWithoutNeovim(SkipNeovimReason.PLUGIN_ERROR)
  fun `test lock list variable 2`() {
    configureByText("\n")
    typeText(commandToKeys("let x = [1]"))
    typeText(commandToKeys("lockvar x"))
    typeText(commandToKeys("let x += [2]"))
    assertPluginError(true)
    assertPluginErrorMessageContains("E741: Value is locked: x")
  }

  fun `test reassigning assigned locked value`() {
    configureByText("\n")
    typeText(commandToKeys("let x = 10"))
    typeText(commandToKeys("lockvar x"))
    typeText(commandToKeys("let y = x"))
    typeText(commandToKeys("echo y"))
    assertExOutput("10\n")
    typeText(commandToKeys("let y = 15"))
    typeText(commandToKeys("echo y"))
    assertExOutput("15\n")
    assertPluginError(false)
  }

  @TestWithoutNeovim(SkipNeovimReason.PLUGIN_ERROR)
  fun `test list elements are also locked`() {
    configureByText("\n")
    typeText(commandToKeys("let x = [1, 2]"))
    typeText(commandToKeys("lockvar x"))
    typeText(commandToKeys("let x[0] = 15"))
    assertPluginError(true)
    assertPluginErrorMessageContains("E741: Value is locked")
    typeText(commandToKeys("echo x"))
    assertExOutput("[1, 2]\n")
  }

  @TestWithoutNeovim(SkipNeovimReason.PLUGIN_ERROR)
  fun `test dict elements are also locked`() {
    configureByText("\n")
    typeText(commandToKeys("let x = {'one': 1}"))
    typeText(commandToKeys("lockvar x"))
    typeText(commandToKeys("let x.two = 2"))
    assertPluginError(true)
    assertPluginErrorMessageContains("E741: Value is locked")
    typeText(commandToKeys("echo x"))
    assertExOutput("{'one': 1}\n")
  }

  @TestWithoutNeovim(SkipNeovimReason.PLUGIN_ERROR)
  fun `test can modify dict elements but not the dict itself`() {
    configureByText("\n")
    typeText(commandToKeys("let x = {'one': 1}"))
    typeText(commandToKeys("lockvar 1 x"))
    typeText(commandToKeys("let x.one = 42"))
    assertPluginError(false)
    typeText(commandToKeys("echo x"))
    assertExOutput("{'one': 42}\n")
    typeText(commandToKeys("let x.two = 2"))
    assertPluginError(true)
    assertPluginErrorMessageContains("E741: Value is locked")
    typeText(commandToKeys("echo x"))
    assertExOutput("{'one': 42}\n")
  }

  @TestWithoutNeovim(SkipNeovimReason.PLUGIN_ERROR)
  fun `test dict elements are also locked 2`() {
    configureByText("\n")
    typeText(commandToKeys("let x = {'one': 1}"))
    typeText(commandToKeys("lockvar x"))
    typeText(commandToKeys("let x['two'] = 2"))
    assertPluginError(true)
    assertPluginErrorMessageContains("E741: Value is locked")
    typeText(commandToKeys("echo x"))
    assertExOutput("{'one': 1}\n")
  }

  fun `test default lock depth`() {
    configureByText("\n")
    typeText(commandToKeys("let x = {'list': [1]}"))
    typeText(commandToKeys("lockvar x"))
    typeText(commandToKeys("let x.list[0] = 42"))
    typeText(commandToKeys("echo x"))
    assertExOutput("{'list': [42]}\n")
  }

  @TestWithoutNeovim(SkipNeovimReason.PLUGIN_ERROR)
  fun `test custom lock depth`() {
    configureByText("\n")
    typeText(commandToKeys("let x = {'list': [1]}"))
    typeText(commandToKeys("lockvar 3 x"))
    typeText(commandToKeys("let x.list[0] = 42"))
    assertPluginError(true)
    assertPluginErrorMessageContains("E741: Value is locked")
    typeText(commandToKeys("echo x"))
    assertExOutput("{'list': [1]}\n")
  }
}
