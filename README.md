# Projeto de Sistemas Distribuídos 2015-2016 #

Grupo de SD 55 - Campus Alameda


Pedro Ascensao 78961 rafascen@gmail.com

Jorge Pessoa 78839 jorge.pessoa@tecnico.ulisboa.pt

Miguel Vera 78980 miguel.coimbra.vera@gmail.com


Repositório:
[tecnico-distsys/A_55-project](https://github.com/tecnico-distsys/A_55-project/)

-------------------------------------------------------------------------------

## Instruções de instalação 


### Ambiente

[0] Iniciar sistema operativo

> O sistema operativo usado como ambiente de desenvolvimento foi Windows

[1] Iniciar servidores de apoio

JUDDI:

```
> O servidor JUDDI deve ser inicializado correndo o ficheiro que se encontra na pasta respetiva da instalacao do JUDDI (tomcat)
     - bin\startup.bat
  que inicializara o servidor de nomes UDDI

```


[2] Criar pasta temporária (pasta exemplo) 

```
cd ~/dev 
mkdir proj-sd-55 

```

[3] Obter código fonte do projeto (versão entregue) a partir da tag de entrega 

```
cd proj-sd-55
git clone -b SD_R1 https://github.com/tecnico-distsys/A_55-project  

cd A_55-project

```

[4] Instalar módulos de bibliotecas auxiliares

```

cd uddi-naming
mvn clean install

```

-------------------------------------------------------------------------------

### Serviço TRANSPORTER

[1] Construir o servidor Transporter e executar duas instancias do Transporter de forma a permitir os testes do client

```
cd  transporter-ws
mvn clean install

mvn exec:java -Dws.i=1
mvn exec:java -Dws.i=2

```

[2] Construir cliente Transporter e executar testes

```

cd transporter-ws-cli
mvn clean install

```

...


-------------------------------------------------------------------------------

### Serviço BROKER

[1] Construir e executar o servidor Broker para permitir os testes de integracao

```
cd broker-ws
mvn clean install
mvn exec:java

```


[2] Construir cliente do Broker e executar testes

```
cd transporter-ws
mvn exec:java -Dws.i=1
mvn exec:java -Dws.i=2

cd ../broker-ws-cli
mvn clean install

```

-------------------------------------------------------------------------------
