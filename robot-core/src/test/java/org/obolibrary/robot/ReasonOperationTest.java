package org.obolibrary.robot;

import static org.junit.Assert.*;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import org.geneontology.reasoner.ExpressionMaterializingReasonerFactory;
import org.junit.Test;
import org.semanticweb.elk.owlapi.ElkReasonerFactory;
import org.semanticweb.owlapi.model.AddImport;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLImportsDeclaration;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.reasoner.OWLReasonerFactory;
import uk.ac.manchester.cs.jfact.JFactFactory;

/** Tests for ReasonOperation. */
public class ReasonOperationTest extends CoreTest {
  /**
   * Test reasoning with StructuralReasoner.
   *
   * @throws Exception on any problem
   */
  @Test
  public void testStructural() throws Exception {
    OWLOntology reasoned = loadOntology("/simple.owl");
    OWLReasonerFactory reasonerFactory =
        new org.semanticweb.owlapi.reasoner.structural.StructuralReasonerFactory();
    ReasonOperation.reasonOperation(reasoned, reasonerFactory);
    assertIdentical("/simple_structural.owl", reasoned);
  }

  /**
   * Test reasoning with ELK.
   *
   * @throws Exception on any problem
   */
  @Test
  public void testELK() throws Exception {
    OWLOntology reasoned = loadOntology("/simple.owl");
    OWLReasonerFactory reasonerFactory = new ElkReasonerFactory();
    ReasonOperation.reasonOperation(reasoned, reasonerFactory);
    assertIdentical("/simple_elk.owl", reasoned);
  }

  /**
   * Test reasoning with HermiT.
   *
   * @throws Exception on any problem
   */
  @Test
  public void testHermit() throws Exception {
    OWLOntology reasoned = loadOntology("/simple.owl");
    OWLReasonerFactory reasonerFactory = new org.semanticweb.HermiT.ReasonerFactory();
    ReasonOperation.reasonOperation(reasoned, reasonerFactory);
    assertEquals(6, reasoned.getAxiomCount());
    assertIdentical("/simple_hermit.owl", reasoned);
  }

  /**
   * Test reasoning with JFact.
   *
   * @throws Exception on any problem
   */
  @Test
  public void testJFact() throws Exception {
    OWLOntology reasoned = loadOntology("/simple.owl");
    OWLReasonerFactory reasonerFactory = new JFactFactory();
    ReasonOperation.reasonOperation(reasoned, reasonerFactory);
    assertEquals(6, reasoned.getAxiomCount());
    assertIdentical("/simple_jfact.owl", reasoned);
  }

  /**
   * Test inferring into new ontology.
   *
   * @throws Exception on any problem
   */
  @Test
  public void testInferIntoNewOntology() throws Exception {
    OWLOntology reasoned = loadOntology("/simple.owl");
    OWLReasonerFactory reasonerFactory = new org.semanticweb.HermiT.ReasonerFactory();
    Map<String, String> opts = new HashMap<>();
    // see https://github.com/ontodev/robot/issues/80
    opts.put("create-new-ontology", "true");
    // see https://github.com/ontodev/robot/issues/80
    opts.put("annotate-inferred-axioms", "true");
    ReasonOperation.reasonOperation(reasoned, reasonerFactory, opts);
    assertEquals(2, reasoned.getAxiomCount());
    // assertIdentical("/simple_hermit.owl", reasoned);
  }

  /**
   * Test inferring into new ontology.
   *
   * @throws Exception on any problem
   */
  @Test
  public void testInferIntoNewOntologyNonTrivial() throws Exception {
    OWLOntology reasoned = loadOntology("/relax_equivalence_axioms_test.obo");
    OWLReasonerFactory reasonerFactory = new org.semanticweb.HermiT.ReasonerFactory();
    Map<String, String> opts = new HashMap<>();

    // see https://github.com/ontodev/robot/issues/80
    opts.put("create-new-ontology", "true");
    opts.put("annotate-inferred-axioms", "true");

    ReasonOperation.reasonOperation(reasoned, reasonerFactory, opts);

    // note that some of the inferred axioms are trivial
    // involving owl:Thing
    assertEquals(15, reasoned.getAxiomCount());
    // assertIdentical("/simple_hermit.owl", reasoned);
  }

  /**
   * Test inferring into new ontology, excluding duplicates.
   *
   * @throws Exception on any problem
   */
  @Test
  public void testInferIntoNewOntologyNoDupes() throws Exception {
    OWLOntology reasoned = loadOntology("/relax_equivalence_axioms_test.obo");
    OWLReasonerFactory reasonerFactory = new org.semanticweb.HermiT.ReasonerFactory();
    Map<String, String> opts = new HashMap<>();
    opts.put("create-new-ontology", "true");
    opts.put("annotate-inferred-axioms", "true");
    opts.put("exclude-duplicate-axioms", "true");
    ReasonOperation.reasonOperation(reasoned, reasonerFactory, opts);
    assertEquals(5, reasoned.getAxiomCount());
    // assertIdentical("/simple_hermit.owl", reasoned);
  }

  /**
   * Test removing redundant subclass axioms.
   *
   * @throws Exception on any problem
   */
  @Test
  public void testRemoveRedundantSubClassAxioms() throws Exception {
    OWLOntology reasoned = loadOntology("/redundant_subclasses.owl");
    OWLReasonerFactory reasonerFactory = new org.semanticweb.elk.owlapi.ElkReasonerFactory();
    ReasonOperation.reasonOperation(reasoned, reasonerFactory, Collections.emptyMap());
    assertIdentical("/redundant_subclasses.owl", reasoned);

    Map<String, String> options = new HashMap<>();
    options.put("remove-redundant-subclass-axioms", "true");

    reasoned = loadOntology("/redundant_subclasses.owl");
    ReasonOperation.reasonOperation(reasoned, reasonerFactory, options);
    assertIdentical("/without_redundant_subclasses.owl", reasoned);
  }

  /**
   * Test reasoning with Expression Materializing Reasoner.
   *
   * <p>This test should return the same results as running any other reasoner
   *
   * @throws Exception on any problem
   */
  @Test
  public void testEMRBasic() throws Exception {
    OWLOntology reasoned = loadOntology("/simple.owl");
    OWLReasonerFactory coreReasonerFactory = new ElkReasonerFactory();
    OWLReasonerFactory reasonerFactory =
        new ExpressionMaterializingReasonerFactory(coreReasonerFactory);
    ReasonOperation.reasonOperation(reasoned, reasonerFactory);
    assertIdentical("/simple_elk.owl", reasoned);
  }

  /**
   * Test reasoning with Expression Materializing Reasoner.
   *
   * <p>This test effectively relaxes an equivalence axiom
   *
   * @throws Exception on any problem
   */
  @Test
  public void testEMRRelax() throws Exception {
    OWLOntology reasoned = loadOntology("/relax_equivalence_axioms_test.obo");
    OWLReasonerFactory coreReasonerFactory = new ElkReasonerFactory();
    OWLReasonerFactory reasonerFactory =
        new ExpressionMaterializingReasonerFactory(coreReasonerFactory);
    Map<String, String> opts = ReasonOperation.getDefaultOptions();
    opts.put("exclude-owl-thing", "true");
    ReasonOperation.reasonOperation(reasoned, reasonerFactory, opts);
    assertIdentical("/relax_equivalence_axioms_expressions_materialized.obo", reasoned);
  }

  /**
   * Test reasoning with intersection axioms
   *
   * @throws Exception on any problem
   */
  @Test
  public void testIntersection() throws Exception {
    OWLOntology reasoned = loadOntology("/intersection.omn");
    OWLReasonerFactory reasonerFactory = new ElkReasonerFactory();
    ReasonOperation.reasonOperation(reasoned, reasonerFactory);
    assertTrue(checkContains(reasoned, "SubClassOf(<http://x.org/XA> <http://x.org/XB>)"));
  }

  /**
   * Test reasoning with external ontologies.
   *
   * <p>Depending on user option, inferred axioms that refer solely to external ontology classes
   * (i.e. those in the import chain) should not be asserted
   *
   * @throws Exception on any problem
   */
  @Test
  public void testExternal() throws Exception {
    OWLOntology importOnt1 = loadOntology("/intersection.omn");
    IRI oiri = importOnt1.getOntologyID().getOntologyIRI().orNull();
    if (oiri == null) {
      throw new Exception("Ontology 'intersection.omn' does not have an IRI");
    }
    OWLOntology mainOnt = loadOntology("/simple.owl");
    OWLOntologyManager mgr = mainOnt.getOWLOntologyManager();
    OWLOntology importOnt = mgr.createOntology(oiri);
    mgr.addAxioms(importOnt, importOnt1.getAxioms());

    OWLImportsDeclaration importsDecl = mgr.getOWLDataFactory().getOWLImportsDeclaration(oiri);
    AddImport ch = new AddImport(mainOnt, importsDecl);
    mgr.applyChange(ch);
    OWLReasonerFactory reasonerFactory = new ElkReasonerFactory();

    Map<String, String> opts = new HashMap<>();
    opts.put("exclude-external-entities", "true");
    ReasonOperation.reasonOperation(mainOnt, reasonerFactory, opts);
    assertFalse(checkContains(mainOnt, "SubClassOf(<http://x.org/XA> <http://x.org/XB>)"));
    opts.put("exclude-external-entities", "false");
    ReasonOperation.reasonOperation(mainOnt, reasonerFactory, opts);
    assertTrue(checkContains(mainOnt, "SubClassOf(<http://x.org/XA> <http://x.org/XB>)"));
  }

  /**
   * Test class axiom generators.
   *
   * @throws Exception on any problem
   */
  @Test
  public void testClassAxiomGenerators() throws Exception {
    OWLOntology ontology = loadOntology("/simple_logic.owl");
    Map<String, String> options = ReasonOperation.getDefaultOptions();
    OWLReasonerFactory reasonerFactory = new JFactFactory();

    options.put("axiom-generators", "EquivalentClass Subclass DisjointClasses");
    ReasonOperation.reasonOperation(ontology, reasonerFactory, options);

    // subclass
    assertTrue(
        checkContains(
            ontology,
            "SubClassOf(<http://purl.obolibrary.org/obo/CLS_04> <http://purl.obolibrary.org/obo/CLS_01>)"));
    // equivalent classes
    assertTrue(
        checkContains(
            ontology,
            "EquivalentClasses(<http://purl.obolibrary.org/obo/CLS_01> <http://purl.obolibrary.org/obo/CLS_02> )"));

    // disjoint classes
    assertTrue(
        checkContains(
            ontology,
            "DisjointClasses(<http://purl.obolibrary.org/obo/CLS_04> <http://purl.obolibrary.org/obo/CLS_06>)"));
  }

  /**
   * Test object property axiom generators.
   *
   * @throws Exception on any problem
   */
  @Test
  public void testObjectPropertyAxiomGenerators() throws Exception {
    OWLOntology ontology = loadOntology("/simple_logic.owl");
    Map<String, String> options = ReasonOperation.getDefaultOptions();
    OWLReasonerFactory reasonerFactory = new JFactFactory();

    options.put(
        "axiom-generators", "EquivalentObjectProperty InverseObjectProperties SubObjectProperty");
    ReasonOperation.reasonOperation(ontology, reasonerFactory, options);

    // subproperty
    assertTrue(
        checkContains(
            ontology,
            "SubObjectPropertyOf(<http://purl.obolibrary.org/obo/OP_05> <http://purl.obolibrary.org/obo/OP_04>)"));
    // equivalent properties
    assertTrue(
        checkContains(
            ontology,
            "EquivalentObjectProperties(<http://purl.obolibrary.org/obo/OP_01> <http://purl.obolibrary.org/obo/OP_04> )"));
    // inverse properties
    assertTrue(
        checkContains(
            ontology,
            "InverseObjectProperties(<http://purl.obolibrary.org/obo/OP_02> <http://purl.obolibrary.org/obo/OP_04>)"));
  }

  /**
   * Test data property axiom generators.
   *
   * @throws Exception on any problem
   */
  @Test
  public void testDataPropertyAxiomGenerators() throws Exception {
    OWLOntology ontology = loadOntology("/simple_logic.owl");
    Map<String, String> options = ReasonOperation.getDefaultOptions();
    OWLReasonerFactory reasonerFactory = new JFactFactory();

    options.put("axiom-generators", "EquivalentDataProperties SubDataProperty");
    ReasonOperation.reasonOperation(ontology, reasonerFactory, options);

    // subproperty
    assertTrue(
        checkContains(
            ontology,
            "SubDataPropertyOf(<http://purl.obolibrary.org/obo/DP_03> <http://purl.obolibrary.org/obo/DP_01>)"));
    // equivalent properties
    assertTrue(
        checkContains(
            ontology,
            "EquivalentDataProperties(<http://purl.obolibrary.org/obo/DP_01> <http://purl.obolibrary.org/obo/DP_04> )"));
  }

  /**
   * Test individual axiom generators.
   *
   * @throws Exception on any problem
   */
  @Test
  public void testIndividualAxiomGenerators() throws Exception {
    OWLOntology ontology = loadOntology("/simple_logic.owl");
    Map<String, String> options = ReasonOperation.getDefaultOptions();
    OWLReasonerFactory reasonerFactory = new JFactFactory();

    options.put("axiom-generators", "ClassAssertion");
    ReasonOperation.reasonOperation(ontology, reasonerFactory, options);

    // class assertion
    assertTrue(
        checkContains(
            ontology,
            "ClassAssertion(<http://purl.obolibrary.org/obo/CLS_02> <http://purl.obolibrary.org/obo/IND_03>)"));
    // property assertion
    assertTrue(
        checkContains(
            ontology,
            "ObjectPropertyAssertion(<http://purl.obolibrary.org/obo/OP_08> <http://purl.obolibrary.org/obo/IND_02> <http://purl.obolibrary.org/obo/IND_03>)"));
  }

  private boolean checkContains(OWLOntology reasoned, String axStr) {
    for (OWLAxiom a : reasoned.getLogicalAxioms()) {
      if (a.toString().equals(axStr)) return true;
    }
    return false;
  }
}
