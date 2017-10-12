package io.gitlab.arturbosch.detekt.api

import org.jetbrains.kotlin.psi.KtFile

abstract class MultiRule : BaseRule() {

	abstract val rules: List<Rule>
	var activeRules: Set<Rule> by SingleAssign()
	var ruleFilters: Set<String> = emptySet()

	override val id: String = javaClass.simpleName

	override fun visitCondition(root: KtFile) = true

	override fun preVisit(root: KtFile) {
		activeRules = rules.filterTo(HashSet()) {
			it.id !in ruleFilters && it.visitCondition(root)
		}
	}

	override fun postVisit(root: KtFile) {
		report(activeRules.flatMap { it.findings })
	}

	fun Rule.runIfActive(block: Rule.() -> Unit) {
		if (this in activeRules) {
			block()
		}
	}
}
