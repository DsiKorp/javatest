package com.dsikorp.epam.javatest.q01.arraylistvslinkedlist.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.dsikorp.epam.javatest.common.verification.CheckResult;
import com.dsikorp.epam.javatest.common.verification.VerificationReport;
import com.dsikorp.epam.javatest.common.verification.VerificationRunner;
import com.dsikorp.epam.javatest.q01.arraylistvslinkedlist.collections.ListType;
import com.dsikorp.epam.javatest.q01.arraylistvslinkedlist.verification.ListContractChecks;

/**
 * Ejecuta la suite de verificación y devuelve el informe.
 */
@Service
public class VerificationService {

	/**
	 * Crea el servicio de verificación.
	 */
	public VerificationService() {
	}

	/**
  * Ejecuta la suite de verificación y devuelve el informe.
  *
  * @return la suite de verificación y devuelve el informe
  */
	public VerificationReport run() {
		long startedAt = System.nanoTime();
		List<CheckResult> results = new ArrayList<>();

		for (ListType type : ListType.values()) {
			results.addAll(VerificationRunner.run(type.name(), ListContractChecks.common(type)));
			if (type == ListType.LINKED_LIST) {
				results.addAll(VerificationRunner.run(type.name(), ListContractChecks.dequeOnly(type)));
			}
		}

		long durationMs = (System.nanoTime() - startedAt) / 1_000_000;
		return VerificationRunner.toReport(results, durationMs);
	}
}
