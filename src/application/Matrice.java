
public class Matrice {
	
	private int[][] matrice;
	private int NbLigne;
	private int NbColonne;
	
	public Matrice(int x, int y) {
		this.matrice = new int[x][y];
		this.NbLigne = x;
		this.NbColonne = y;
	}
	
	public Matrice(Matrice m) {
		this.NbLigne = m.getNbLigne();
		this.NbColonne = m.getNbColonne();
		this.matrice = new int[NbLigne][NbColonne];
		for (int i =0; i < NbLigne; i++) {
			for (int j = 0; j < NbColonne; j++) {
				this.matrice[i][j] = m.getMatrice()[i][j];
			}
		}
		
	}
	
	
	public int getNbLigne() {
		return NbLigne;
	}
	
	public int getNbColonne() {
		return NbColonne;
	}
	
	public int[][] getMatrice() {
		return matrice;
	}
	
	public boolean EstVide() {
		return this.NbLigne == 0 && this.NbColonne == 0;
	}
	
	public int[] getColonne(int y) {
		int[] reponse = new int[NbLigne];
		for (int i = 0; i < NbLigne; i++) {
			reponse[i] = this.matrice[i][y];
		}
		return reponse;
	}
	
	public int[] getLigne(int x) {
		int[] reponse = new int[NbColonne];
		for (int i = 0; i < NbColonne; i++) {
			reponse[i] = this.matrice[x][i];
		}
		return reponse;
	}
	
	public int get(int x, int y) {
		return this.matrice[x][y];
	}
	
	public void set(int x, int y, int valeur) {
		this.matrice[x][y] = valeur;
	}
	
	/*efface une ligne de la matrice*/
	public void RmLigne(int x) {
		int[][] nouvellematrice = new int[NbLigne-1][NbColonne];
		for (int i = 0; i < NbLigne; i++) {
			if (i != x) {
				for (int j = 0; j < NbColonne;j++) {
					if (i<x) {
						nouvellematrice[i][j] = this.matrice[i][j];
					} else {
						nouvellematrice[i-1][j] = this.matrice[i][j];
					}
				}
			}
		}
		this.matrice = nouvellematrice;
		this.NbLigne = NbLigne-1;
		if (NbLigne == 0) {
			NbColonne = 0;
		}
	}
	
	/*efface une colonne de la matrice*/
	public void RmColonne(int y) {
		int[][] nouvellematrice = new int[NbLigne][NbColonne-1];
		for (int i = 0; i < NbLigne; i++) {
			for (int j = 0; j < NbColonne;j++) {
				if (j<y) {
					nouvellematrice[i][j] = this.matrice[i][j];
				} else {
					if (j > y) {
						nouvellematrice[i][j-1] = this.matrice[i][j];
					}
				}
			}
		}
		this.matrice = nouvellematrice;
		this.NbColonne = NbColonne-1;
		if (NbColonne == 0) {
			NbLigne = 0;
		}
	}

}
