<?php
    class DBConnection{

        private $host = DB_HOST;
        private $name = DB_NAME;
        private $user = DB_USER;
        private $pass = DB_PASS;
        private $dbh;
        private $error = '';
        
        public function __construct(){
            $dsn = 'mysql:host='.$this->host.';dbname='.$this->name; 
            $options = array(
                PDO::ATTR_ERRMODE => PDO::ERRMODE_EXCEPTION
            );  
            try {
                $this->dbh = new PDO($dsn, $this->user,  $this->pass, $options);
            } catch(PDOException $e) {
                $this->error = $e->getMessage();
            }
        }

        public function __toString(){
            return $this->error;
        }

        public function runQuery($sql){
            try {
                $num = $this->dbh->exec($sql);
            } catch (PDOException $e) {
                $this->error = $e->getMessage();
            }
            return $num;
        }

        public function getQuery($sql){
            try {
                $stmt = $this->dbh->query($sql);
                $stmt->setFetchMode(PDO::FETCH_ASSOC);
                return $stmt;
            } catch (PDOException $e) {
                $this->error = $e->getMessage();
            }
        }

        public function getDBH(){
            return $this->dbh;
        }

    }    

?>