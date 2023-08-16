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
  
