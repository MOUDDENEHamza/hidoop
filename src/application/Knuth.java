import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;

public class Knuth {
	
	private Collection<Collection<Integer>> solution;
	private int lignestotal;
	
	public Knuth(int nblignestotal) {
		solution = new ArrayList<Collection<Integer>>();
		lignestotal = nblignestotal;
	}
	
	public Collection<Collection<Integer>> getSolution() {
		return solution;
	}
	
	/*renvoie le numéro de la première colonne qui contient le minimum de 1*/
	public int ColonneMin1(Matrice matrice) {
		int mini = matrice.getNbLigne() + 1;
		int colmini = -1;
		int nb1;
		for (int j = 0; j< matrice.getNbColonne(); j++) {
			nb1 = 0;
			for (int i = 0; i< matrice.getNbLigne(); i++) {
				if (matrice.get(i,j) == 1) {
					nb1++;
				}
			}
			if (nb1 < mini) {
				mini = nb1;
				colmini = j;
			}
		}
		return colmini;
	}
	
	public Collection<Integer> ProgPrincipal(Matrice matrice, Collection<Integer> solcourante) {
		Collection<Integer> sol;
		int nb1;
		if (!matrice.EstVide()) {
			int c = ColonneMin1(matrice);
			nb1 = 0;
			for (int l = 0; l < matrice.getNbLigne(); l++) {
				if (matrice.get(l,c)==1) {
					nb1++;
					sol = ProgAnn(new Matrice(matrice), l, new ArrayList<Integer>(solcourante));
					System.out.println(sol);
					//si la solution est valide, on l'ajoute après avoir enlevé -1
					if(sol.contains(-1)) {
						sol.remove(-1);
						solution.add(sol);
					}
				}
			}
			//si il n'y avait pas de 1, on doit s'arreter
			if (nb1==0) {
				return solcourante;
			}
			
		} else {
			//on indique que ca a finit avec succes
			solcourante.add(-1);
		}
		return solcourante;
	}
	
	public Collection<Integer> ProgAnn(Matrice matrice, int l, Collection<Integer> solcourante) {
		int nbligne = matrice.getNbLigne();
		int nbColonne = matrice.getNbColonne();
		LinkedList<Integer> LigneASupprimer = new LinkedList<Integer>();
		LinkedList<Integer> ColonneASupprimer = new LinkedList<Integer>();
		//on ajoute la ligne l à la solution
		//le '+' sert à donner le bon numéro de la ligne
		solcourante.add(l + (lignestotal -nbligne));
		//on repertorie leslignes et colonnes à supprimer
		for (int c = 0; c < nbColonne ; c++) {
			if (matrice.get(l,c) == 1) {
				for (int i = 0; i<nbligne; i++) {
					if (matrice.get(i,c) == 1) {
						if (!LigneASupprimer.contains(i)) {
							LigneASupprimer.add(i);
						}
					}
				}
				if (!ColonneASupprimer.contains(c)) {
					ColonneASupprimer.add(c);
				}
			}
		}
		//on supprime les lignes et colonnes
		for (int i = LigneASupprimer.size() - 1; i >=0; i--) {
			matrice.RmLigne(LigneASupprimer.get(i));
		}
		if (!matrice.EstVide()) {
			for (int i = ColonneASupprimer.size()-1; i >=0; i--) {
					matrice.RmColonne(ColonneASupprimer.get(i));
			}
		}
		return ProgPrincipal(matrice, solcourante);
	}
	
	public static void main (String args []) {
		Knuth k = new Knuth(6);
		Matrice m = new Matrice(6,7);
		for (int i =0; i < 6; i++) {
			for (int j = 0; j < 7; j++) {
				m.set(i,j,0);
			}
		}
		m.set(0,0,1);
		m.set(0,3,1);
		m.set(0,6,1);
		m.set(1,0,1);
		m.set(1,3,1);
		m.set(2,3,1);
		m.set(2,4,1);
		m.set(2,6,1);
		m.set(3,2,1);
		m.set(3,4,1);
		m.set(3,5,1);
		m.set(4,1,1);
		m.set(4,2,1);
		m.set(4,5,1);
		m.set(4,6,1);
		m.set(5,1,1);
		m.set(5,6,1);
		Collection<Integer> s = k.ProgPrincipal(m,new ArrayList<Integer>());
		Collection<Collection<Integer>> sol = k.getSolution();
		System.out.println(sol);
	}
}
