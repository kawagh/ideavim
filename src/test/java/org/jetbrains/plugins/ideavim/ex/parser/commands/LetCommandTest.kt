/*
 * Copyright 2003-2023 The IdeaVim authors
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE.txt file or at
 * https://opensource.org/licenses/MIT.
 */

package org.jetbrains.plugins.ideavim.ex.parser.commands

import com.maddyhome.idea.vim.api.injector
import com.maddyhome.idea.vim.vimscript.model.commands.LetCommand
import com.maddyhome.idea.vim.vimscript.model.expressions.Register
import com.maddyhome.idea.vim.vimscript.model.expressions.SimpleExpression
import com.maddyhome.idea.vim.vimscript.model.expressions.operators.AssignmentOperator
import junit.framework.TestCase.assertTrue
import org.junit.Test
import kotlin.test.assertEquals

class LetCommandTest {

  @Test
  fun `let with register is parsed correctly`() {
    val script = injector.vimscriptParser.parse("let @+=5")
    assertEquals(1, script.units.size)
    val command = script.units.first()
    assertTrue(command is LetCommand)
    command as LetCommand
    assertEquals(Register('+'), command.variable)
    assertEquals(AssignmentOperator.ASSIGNMENT, command.operator)
    assertEquals(SimpleExpression(5), command.expression)
  }

  @Test
  fun `let with register is parsed correctly 2`() {
    val script = injector.vimscriptParser.parse("let @--=42")
    assertEquals(1, script.units.size)
    val command = script.units.first()
    assertTrue(command is LetCommand)
    command as LetCommand
    assertEquals(Register('-'), command.variable)
    assertEquals(AssignmentOperator.SUBTRACTION, command.operator)
    assertEquals(SimpleExpression(42), command.expression)
  }
}
