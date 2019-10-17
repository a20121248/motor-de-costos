DROP INDEX MS_CASCADA_IDX;
DROP INDEX MS_OBJETO_LINEAS_IDX;
CREATE INDEX MS_CASCADA_IDX ON MS_CASCADA(REPARTO_TIPO,PERIODO,CENTRO_CODIGO);
CREATE INDEX MS_OBJETO_LINEAS_IDX ON MS_OBJETO_LINEAS(REPARTO_TIPO,PERIODO,PRODUCTO_CODIGO,SUBCANAL_CODIGO);--FALTA AGREGARLE EL CAMPO CENTRO_CODIGO

DROP TABLE PACIFICO_DEV.MS_CASCADA;
CREATE TABLE PACIFICO_DEV.MS_CASCADA (
  CENTRO_CODIGO VARCHAR2(8) NOT NULL,
  PERIODO NUMBER(6) NOT NULL,
  ITERACION NUMBER(10) NOT NULL,
  SALDO NUMBER(35,15) NOT NULL,
  ENTIDAD_ORIGEN_CODIGO VARCHAR2(10) NOT NULL,
  GRUPO_GASTO CHAR(2),
  REPARTO_TIPO NUMBER(1) NOT NULL,
  CUENTA_CONTABLE_ORIGEN_CODIGO VARCHAR2(15 BYTE) NOT NULL,
  PARTIDA_ORIGEN_CODIGO VARCHAR2(6 BYTE) NOT NULL ,
  CENTRO_ORIGEN_CODIGO VARCHAR2(8) NOT NULL
) PARTITION BY RANGE (PERIODO) (
  PARTITION P_201901 VALUES LESS THAN (201902),
  PARTITION P_201902 VALUES LESS THAN (201903),
  PARTITION P_201903 VALUES LESS THAN (201904),
  PARTITION P_201904 VALUES LESS THAN (201905),
  PARTITION P_201905 VALUES LESS THAN (201906),
  PARTITION P_201906 VALUES LESS THAN (201907),
  PARTITION P_201907 VALUES LESS THAN (201908),
  PARTITION P_201908 VALUES LESS THAN (201909),
  PARTITION P_201909 VALUES LESS THAN (201910),
  PARTITION P_201910 VALUES LESS THAN (201911),
  PARTITION P_201911 VALUES LESS THAN (201912),
  PARTITION P_201912 VALUES LESS THAN (201913),
  PARTITION P_202001 VALUES LESS THAN (202002),
  PARTITION P_202002 VALUES LESS THAN (202003),
  PARTITION P_202003 VALUES LESS THAN (202004),
  PARTITION P_202004 VALUES LESS THAN (202005),
  PARTITION P_202005 VALUES LESS THAN (202006),
  PARTITION P_202006 VALUES LESS THAN (202007),
  PARTITION P_202007 VALUES LESS THAN (202008),
  PARTITION P_202008 VALUES LESS THAN (202009),
  PARTITION P_202009 VALUES LESS THAN (202010),
  PARTITION P_202010 VALUES LESS THAN (202011),
  PARTITION P_202011 VALUES LESS THAN (202012),
  PARTITION P_202012 VALUES LESS THAN (202013),
  PARTITION P_202101 VALUES LESS THAN (202102),
  PARTITION P_202102 VALUES LESS THAN (202103),
  PARTITION P_202103 VALUES LESS THAN (202104),
  PARTITION P_202104 VALUES LESS THAN (202105),
  PARTITION P_202105 VALUES LESS THAN (202106),
  PARTITION P_202106 VALUES LESS THAN (202107),
  PARTITION P_202107 VALUES LESS THAN (202108),
  PARTITION P_202108 VALUES LESS THAN (202109),
  PARTITION P_202109 VALUES LESS THAN (202110),
  PARTITION P_202110 VALUES LESS THAN (202111),
  PARTITION P_202111 VALUES LESS THAN (202112),
  PARTITION P_202112 VALUES LESS THAN (202113),
  PARTITION P_202201 VALUES LESS THAN (202202),
  PARTITION P_202202 VALUES LESS THAN (202203),
  PARTITION P_202203 VALUES LESS THAN (202204),
  PARTITION P_202204 VALUES LESS THAN (202205),
  PARTITION P_202205 VALUES LESS THAN (202206),
  PARTITION P_202206 VALUES LESS THAN (202207),
  PARTITION P_202207 VALUES LESS THAN (202208),
  PARTITION P_202208 VALUES LESS THAN (202209),
  PARTITION P_202209 VALUES LESS THAN (202210),
  PARTITION P_202210 VALUES LESS THAN (202211),
  PARTITION P_202211 VALUES LESS THAN (202212),
  PARTITION P_202212 VALUES LESS THAN (202213),
  PARTITION P_202301 VALUES LESS THAN (202302),
  PARTITION P_202302 VALUES LESS THAN (202303),
  PARTITION P_202303 VALUES LESS THAN (202304),
  PARTITION P_202304 VALUES LESS THAN (202305),
  PARTITION P_202305 VALUES LESS THAN (202306),
  PARTITION P_202306 VALUES LESS THAN (202307),
  PARTITION P_202307 VALUES LESS THAN (202308),
  PARTITION P_202308 VALUES LESS THAN (202309),
  PARTITION P_202309 VALUES LESS THAN (202310),
  PARTITION P_202310 VALUES LESS THAN (202311),
  PARTITION P_202311 VALUES LESS THAN (202312),
  PARTITION P_202312 VALUES LESS THAN (202313),
  PARTITION P_202401 VALUES LESS THAN (202402),
  PARTITION P_202402 VALUES LESS THAN (202403),
  PARTITION P_202403 VALUES LESS THAN (202404),
  PARTITION P_202404 VALUES LESS THAN (202405),
  PARTITION P_202405 VALUES LESS THAN (202406),
  PARTITION P_202406 VALUES LESS THAN (202407),
  PARTITION P_202407 VALUES LESS THAN (202408),
  PARTITION P_202408 VALUES LESS THAN (202409),
  PARTITION P_202409 VALUES LESS THAN (202410),
  PARTITION P_202410 VALUES LESS THAN (202411),
  PARTITION P_202411 VALUES LESS THAN (202412),
  PARTITION P_202412 VALUES LESS THAN (202413)
);

DROP TABLE PACIFICO_DEV.MS_OBJETO_LINEAS;
CREATE TABLE PACIFICO_DEV.MS_OBJETO_LINEAS
(
  SUBCANAL_CODIGO VARCHAR2(10 BYTE) NOT NULL,
  PRODUCTO_CODIGO VARCHAR2(10 BYTE) NOT NULL,
  PERIODO NUMBER(6, 0) NOT NULL,
  CENTRO_CODIGO VARCHAR2(10 BYTE) NOT NULL,
  ENTIDAD_ORIGEN_CODIGO VARCHAR2(10 BYTE) NOT NULL,
  SALDO NUMBER(35, 15) NOT NULL,
  CUENTA_CONTABLE_ORIGEN_CODIGO VARCHAR2(15 BYTE) NOT NULL,
  PARTIDA_ORIGEN_CODIGO VARCHAR2(6 BYTE) NOT NULL ,
  CENTRO_ORIGEN_CODIGO VARCHAR2(8) NOT NULL,
  reparto_tipo NUMBER(1) NOT NULL,
  FECHA_CREACION DATE,
  FECHA_ACTUALIZACION DATE,
  GRUPO_GASTO CHAR(2 BYTE) NOT NULL,
  DRIVER_CODIGO VARCHAR2(10 BYTE) NOT NULL
) PARTITION BY RANGE (PERIODO) (
  PARTITION P_201901 VALUES LESS THAN (201902),
  PARTITION P_201902 VALUES LESS THAN (201903),
  PARTITION P_201903 VALUES LESS THAN (201904),
  PARTITION P_201904 VALUES LESS THAN (201905),
  PARTITION P_201905 VALUES LESS THAN (201906),
  PARTITION P_201906 VALUES LESS THAN (201907),
  PARTITION P_201907 VALUES LESS THAN (201908),
  PARTITION P_201908 VALUES LESS THAN (201909),
  PARTITION P_201909 VALUES LESS THAN (201910),
  PARTITION P_201910 VALUES LESS THAN (201911),
  PARTITION P_201911 VALUES LESS THAN (201912),
  PARTITION P_201912 VALUES LESS THAN (201913),
  PARTITION P_202001 VALUES LESS THAN (202002),
  PARTITION P_202002 VALUES LESS THAN (202003),
  PARTITION P_202003 VALUES LESS THAN (202004),
  PARTITION P_202004 VALUES LESS THAN (202005),
  PARTITION P_202005 VALUES LESS THAN (202006),
  PARTITION P_202006 VALUES LESS THAN (202007),
  PARTITION P_202007 VALUES LESS THAN (202008),
  PARTITION P_202008 VALUES LESS THAN (202009),
  PARTITION P_202009 VALUES LESS THAN (202010),
  PARTITION P_202010 VALUES LESS THAN (202011),
  PARTITION P_202011 VALUES LESS THAN (202012),
  PARTITION P_202012 VALUES LESS THAN (202013),
  PARTITION P_202101 VALUES LESS THAN (202102),
  PARTITION P_202102 VALUES LESS THAN (202103),
  PARTITION P_202103 VALUES LESS THAN (202104),
  PARTITION P_202104 VALUES LESS THAN (202105),
  PARTITION P_202105 VALUES LESS THAN (202106),
  PARTITION P_202106 VALUES LESS THAN (202107),
  PARTITION P_202107 VALUES LESS THAN (202108),
  PARTITION P_202108 VALUES LESS THAN (202109),
  PARTITION P_202109 VALUES LESS THAN (202110),
  PARTITION P_202110 VALUES LESS THAN (202111),
  PARTITION P_202111 VALUES LESS THAN (202112),
  PARTITION P_202112 VALUES LESS THAN (202113),
  PARTITION P_202201 VALUES LESS THAN (202202),
  PARTITION P_202202 VALUES LESS THAN (202203),
  PARTITION P_202203 VALUES LESS THAN (202204),
  PARTITION P_202204 VALUES LESS THAN (202205),
  PARTITION P_202205 VALUES LESS THAN (202206),
  PARTITION P_202206 VALUES LESS THAN (202207),
  PARTITION P_202207 VALUES LESS THAN (202208),
  PARTITION P_202208 VALUES LESS THAN (202209),
  PARTITION P_202209 VALUES LESS THAN (202210),
  PARTITION P_202210 VALUES LESS THAN (202211),
  PARTITION P_202211 VALUES LESS THAN (202212),
  PARTITION P_202212 VALUES LESS THAN (202213),
  PARTITION P_202301 VALUES LESS THAN (202302),
  PARTITION P_202302 VALUES LESS THAN (202303),
  PARTITION P_202303 VALUES LESS THAN (202304),
  PARTITION P_202304 VALUES LESS THAN (202305),
  PARTITION P_202305 VALUES LESS THAN (202306),
  PARTITION P_202306 VALUES LESS THAN (202307),
  PARTITION P_202307 VALUES LESS THAN (202308),
  PARTITION P_202308 VALUES LESS THAN (202309),
  PARTITION P_202309 VALUES LESS THAN (202310),
  PARTITION P_202310 VALUES LESS THAN (202311),
  PARTITION P_202311 VALUES LESS THAN (202312),
  PARTITION P_202312 VALUES LESS THAN (202313),
  PARTITION P_202401 VALUES LESS THAN (202402),
  PARTITION P_202402 VALUES LESS THAN (202403),
  PARTITION P_202403 VALUES LESS THAN (202404),
  PARTITION P_202404 VALUES LESS THAN (202405),
  PARTITION P_202405 VALUES LESS THAN (202406),
  PARTITION P_202406 VALUES LESS THAN (202407),
  PARTITION P_202407 VALUES LESS THAN (202408),
  PARTITION P_202408 VALUES LESS THAN (202409),
  PARTITION P_202409 VALUES LESS THAN (202410),
  PARTITION P_202410 VALUES LESS THAN (202411),
  PARTITION P_202411 VALUES LESS THAN (202412),
  PARTITION P_202412 VALUES LESS THAN (202413)
);
