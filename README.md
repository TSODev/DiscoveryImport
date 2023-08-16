# DiscoveryImport

-----

exemple de ligne de commande: (nécessite un JVM):

--server="https://SERVER/api/v1.9/" --username=UNSERNAME --password=PASSWORD --file=MultipleImport.xlsx --debug=all

--server="https://SERVER/api/v1.9/" --username=USERNAME --password=PASSWORD -g --file=ImportTemplate.xlsx --debug=info 


-----

  Ecrit des données dans BMC Discovery

Options:

  **-s, --server=\<text>**    URL API du serveur Discovery , (https et termine avec
                         '/') généralement https://server/api/v1.1/
                         
  **-u, --username=\<text>**  Nom de l'utilisateur
  
  **-p, --password=\<text>**  Mot de Passe de l'utilisateur
  
  **-x, --unsecure**         pas de vérification du certificat SSL (permet
                         l'utilisation de certificat auto signé)
                         
  **-f, --file=\<path>**      path du fichier source à importer
  
  **-a, --params=\<text>**    Objet JSON à traiter
  
  **-e, --explain**          explique la commande
  
  **-g, --generate**        Crée un fichier Excel de modele d'import
  
  **-d, --debug=( off | trace | debug | info | error | fatal | all )**
                         Niveau du debug
                         
  **--version**              Show the version and exit
  
  **-h, --help**             Show this message and exit
  
-----

#####Générer un fichier modèle 

Il est possible de générer le fichier modèle d'import en utilisant l'option '--generate'

Le fichier excel créé contient alors une suite d'onglets représentant le modèle de données de chacun des noeuds de la taxonomy de BMC Discovery

* La première rangée défini le nom de l'attribut

* La seconde rangée défini le type de donnée pour cet attribut 
* La troisième rangée donne la définition BMC Discovery pour cet attribut
* Les données à importer doivent être complétées depuis la cinquième rangée


#####Utiliser un fichier modèle

Je vous recommande de copier le ou les onglets qui vous intéresse vers un nouveau fichier excel vide, de compléter les cellules en commençant à la rangées 5 (les types de cellule sont normalement configurés sur la rangées 5) et de procéder à l'import du fichier excel résultant


#####Importer un fichier de données

Le programme d'import importe tout les onglets du fichier spécifié dans des noeuds définis. par les noms des onglets du fichiers

