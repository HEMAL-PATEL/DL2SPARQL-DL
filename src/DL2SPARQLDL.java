/*
 * DL2SPARQL-DL 1.0
 * Developed by Adriel Café <ac@adrielcafe.com>
 * 
 * MIT Licence
 */
package com.adrielcafe.sw;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.Pattern;

public class DL2SPARQLDL {
	private static HashMap<QueryPattern, Pattern> patterns = new HashMap<QueryPattern, Pattern>();
	private static ArrayList<String> variables = null;
	
	private static enum QueryPattern {
		Class,
		Property,
		Individual,
		Type,
		Transitive,
		SubClassOf,
		EquivalentClass,
		DisjointWith,
		SubPropertyOf,
		EquivalentProperty,
		PropertyValue,
		SameAs,
		DifferentFrom,
		InverseOf
	}
	
	static {
		patterns.put(QueryPattern.Class, Pattern.compile("\\??[A-Z+][\\w]*"));
		patterns.put(QueryPattern.Property, Pattern.compile("\\??[a-z+][\\w]*"));
		patterns.put(QueryPattern.Individual, Pattern.compile("\\{\\??[\\w]*\\}"));
		patterns.put(QueryPattern.Type, Pattern.compile("\\??[A-Z+][\\w]*∈[A-Z+][\\w]*"));
//		patterns.put(QueryPattern.ComplementOf, Pattern.compile("¬\\??[A-Z+][\\w]*"));
//		patterns.put(QueryPattern.Intersection, Pattern.compile("\\??[A-Z+][\\w]*∩\\??[A-Z+][\\w]*"));
//		patterns.put(QueryPattern.Union, Pattern.compile("\\??[A-Z+][\\w]*∪\\??[A-Z+][\\w]*"));
//		patterns.put(QueryPattern.UniversalRestriction, Pattern.compile("∀\\??[a-z+][\\w]*.\\??[A-Z+][\\w]*"));
//		patterns.put(QueryPattern.ExistentialRestriction, Pattern.compile("∃\\??[a-z+][\\w]*.\\??[A-Z+][\\w]*"));
//		patterns.put(QueryPattern.MaxCardinality, Pattern.compile("≤[\\d]+ \\??[a-z+][\\w]*"));
//		patterns.put(QueryPattern.MinCardinality, Pattern.compile("≥[\\d]+ \\??[a-z+][\\w]*"));
		patterns.put(QueryPattern.Transitive, Pattern.compile("\\??[a-z+][\\w]*\\+"));
		patterns.put(QueryPattern.SubClassOf, Pattern.compile("\\??[A-Z+][\\w]*⊆\\??[A-Z+][\\w]*"));
		patterns.put(QueryPattern.EquivalentClass, Pattern.compile("\\??[A-Z+][\\w]*≡\\??[A-Z+][\\w]*"));
		patterns.put(QueryPattern.DisjointWith, Pattern.compile("\\??[A-Z+][\\w]*⊆¬\\??[A-Z+][\\w]*"));
		patterns.put(QueryPattern.SubPropertyOf, Pattern.compile("\\??[a-z+][\\w]*⊆\\??[a-z+][\\w]*"));
		patterns.put(QueryPattern.EquivalentProperty, Pattern.compile("\\??[a-z+][\\w]*≡\\??[a-z+][\\w]*"));
		patterns.put(QueryPattern.PropertyValue, Pattern.compile("\\??[a-z+][\\w]*\\(\\??[\\w]*,\\??[\\w]*\\)"));
		patterns.put(QueryPattern.SameAs, Pattern.compile("\\{\\??[\\w]*\\}≡\\{\\??[\\w]*\\}"));
		patterns.put(QueryPattern.DifferentFrom, Pattern.compile("\\{\\??[\\w]*\\}⊆¬\\{\\??[\\w]*\\}"));
		patterns.put(QueryPattern.InverseOf, Pattern.compile("\\??[a-z+][\\w]*≡\\??[a-z+][\\w]*-"));
	}
	
	public DL2SPARQLDL(){
		
	}
	
	public static String parse(String dlQuery, String prefix, String sufix){
		String sparqldlQuery = "";
		String queryVariables = "";
		String[] axioms = dlQuery.replaceAll(" ", "").split("∩");
		
		variables = new ArrayList<String>();
		
		try {
			for (String axiom : axioms) {
				for (QueryPattern pattern : patterns.keySet()) {
					if(patterns.get(pattern).matcher(axiom).matches())
						sparqldlQuery += axiomToPattern(axiom, prefix, pattern) + ", ";
				}
			}
			
			sparqldlQuery = sparqldlQuery.substring(0, sparqldlQuery.length() - 2);
			queryVariables = variables.toString().replace("[", "").replace("]",  "");
		} catch (Exception e) { 
			return "PARSER ERROR: " + e.getMessage();
		}
		
		return String.format("PREFIX %s: <%s> \nSELECT %s WHERE { %s \n}", prefix, sufix, queryVariables.replaceAll(",", ""), sparqldlQuery);
	}
	
	private static String axiomToPattern(String axiom, String prefix, QueryPattern pattern) {
		String queryPattern = "";
		String aux1, aux2, aux3;
		String[] aux;
		switch (pattern){
			case Class:
 				aux1 = addPrefixToAtom(axiom, prefix);
				queryPattern = String.format("Class(%s)", aux1);
				break;
			case Property:
 				aux1 = addPrefixToAtom(axiom, prefix);
				queryPattern = String.format("Property(%s)", aux1);
				break;
 			case Individual:
 				aux1 = axiom.substring(1, axiom.length() - 1);
 				aux1 = addPrefixToAtom(aux1, prefix);
				queryPattern = String.format("Individual(%s)", aux1);
				break;
			case Type:
				aux = axiom.split("∈");
 				aux1 = addPrefixToAtom(aux[0], prefix);
 				aux2 = addPrefixToAtom(aux[1], prefix);
				queryPattern = String.format("Type(%s, %s)", aux1, aux2);
				break;
			case Transitive:
				aux1 = axiom.substring(0, axiom.length() - 1);
 				aux1 = addPrefixToAtom(aux1, prefix);
				queryPattern = String.format("Transitive(%s)", aux1);
				break;
			case SubClassOf:
				aux = axiom.split("⊆");
 				aux1 = addPrefixToAtom(aux[0], prefix);
 				aux2 = addPrefixToAtom(aux[1], prefix);
				queryPattern = String.format("SubClassOf(%s, %s)", aux1, aux2);
				break;
			case EquivalentClass:
				aux = axiom.split("≡");
 				aux1 = addPrefixToAtom(aux[0], prefix);
 				aux2 = addPrefixToAtom(aux[1], prefix);
				queryPattern = String.format("EquivalentClass(%s, %s)", aux1, aux2);
				break;
			case DisjointWith:
				aux = axiom.split("⊆¬");
 				aux1 = addPrefixToAtom(aux[0], prefix);
 				aux2 = addPrefixToAtom(aux[1], prefix);
				queryPattern = String.format("DisjointWith(%s, %s)", aux1, aux2);				
				break;
			case SubPropertyOf:
				aux = axiom.split("⊆");
 				aux1 = addPrefixToAtom(aux[0], prefix);
 				aux2 = addPrefixToAtom(aux[1], prefix);
				queryPattern = String.format("SubPropertyOf(%s, %s)", aux1, aux2);
				break;
			case EquivalentProperty:
				aux = axiom.split("≡");
 				aux1 = addPrefixToAtom(aux[0], prefix);
 				aux2 = addPrefixToAtom(aux[1], prefix);
				queryPattern = String.format("EquivalentProperty(%s, %s)", aux1, aux2);
				break;
			case PropertyValue:
				aux1 = axiom.substring(axiom.indexOf("(") + 1, axiom.indexOf(","));
 				aux1 = addPrefixToAtom(aux1, prefix);
				aux2 = axiom.substring(0, axiom.indexOf("("));
 				aux2 = addPrefixToAtom(aux2, prefix);
				aux3 = axiom.substring(axiom.indexOf(",") + 1, axiom.indexOf(")"));
 				aux3 = addPrefixToAtom(aux3, prefix);
				queryPattern = String.format("PropertyValue(%s, %s, %s)", aux1, aux2, aux3 );
				break;
			case SameAs:
				aux = axiom.split("≡");
				aux1 = aux[0].substring(1, aux[0].length() - 1);
 				aux1 = addPrefixToAtom(aux1, prefix);
				aux2 = aux[1].substring(1, aux[1].length() - 1);
 				aux2 = addPrefixToAtom(aux2, prefix);
				queryPattern = String.format("SameAs(%s, %s)", aux1, aux2);
				break;
			case DifferentFrom:
				aux = axiom.split("⊆¬");
				aux1 = aux[0].substring(1, aux[0].length() - 1);
 				aux1 = addPrefixToAtom(aux1, prefix);
				aux2 = aux[1].substring(1, aux[1].length() - 1);
 				aux2 = addPrefixToAtom(aux2, prefix);
				queryPattern = String.format("DifferentFrom(%s, %s)", aux1, aux2);
				break;
			case InverseOf:
				aux = axiom.split("≡");
 				aux1 = addPrefixToAtom(aux[0], prefix);
 				aux2 = addPrefixToAtom(aux[1].substring(0, aux[1].length() - 1), prefix);
				queryPattern = String.format("InverseOf(%s, %s)", aux1, aux2);
				break;
			default:
				break;
		}
		
		return "\n" + queryPattern;
	}
	
	private static String addPrefixToAtom(String atom, String prefix){
		if (atom.startsWith("?") && !variables.contains(atom))
			variables.add(atom);
		return atom.startsWith("?") ? atom : prefix + ":" + atom;
	}
}