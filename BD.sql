DROP TABLE SEGURIDAD_PERMISOS CASCADE CONSTRAINTS;
DROP TABLE SEGURIDAD_ROLES CASCADE CONSTRAINTS;
DROP TABLE SEGURIDAD_PERMISO_ROL CASCADE CONSTRAINTS;
DROP TABLE SEGURIDAD_USUARIOS CASCADE CONSTRAINTS;
DROP TABLE PLAN_DE_CUENTAS CASCADE CONSTRAINTS;
DROP TABLE PLAN_DE_CUENTA_LINEAS CASCADE CONSTRAINTS;
DROP TABLE CENTRO_NIVELES CASCADE CONSTRAINTS;
DROP TABLE CENTRO_TIPOS CASCADE CONSTRAINTS;
DROP TABLE CENTROS CASCADE CONSTRAINTS;
DROP TABLE CENTRO_LINEAS CASCADE CONSTRAINTS;
DROP TABLE ENTIDAD_TIPOS CASCADE CONSTRAINTS;
DROP TABLE DRIVER_TIPOS CASCADE CONSTRAINTS;
DROP TABLE DRIVERS CASCADE CONSTRAINTS;
DROP TABLE DRIVER_LINEAS CASCADE CONSTRAINTS;
DROP TABLE ENTIDAD_ORIGEN_DRIVER CASCADE CONSTRAINTS;
DROP TABLE BANCA_GRUPOS CASCADE CONSTRAINTS;
DROP TABLE OFICINA_GRUPOS CASCADE CONSTRAINTS;
DROP TABLE PRODUCTO_GRUPOS CASCADE CONSTRAINTS;
DROP TABLE JERARQUIA CASCADE CONSTRAINTS;
DROP TABLE PRODUCTOS CASCADE CONSTRAINTS;
DROP TABLE PRODUCTO_LINEAS CASCADE CONSTRAINTS;
DROP TABLE BANCAS CASCADE CONSTRAINTS;
DROP TABLE BANCA_LINEAS CASCADE CONSTRAINTS;
DROP TABLE OFICINAS CASCADE CONSTRAINTS;
DROP TABLE OFICINA_LINEAS CASCADE CONSTRAINTS;
DROP TABLE DRIVER_OBCO_LINEAS CASCADE CONSTRAINTS;
DROP TABLE OBCO_LINEAS CASCADE CONSTRAINTS;
DROP TABLE EJECUCIONES CASCADE CONSTRAINTS;
DROP TABLE JMD_REP_GRUPO_SALDO CASCADE CONSTRAINTS;
DROP TABLE JMD_REP_ADM_OPE_M CASCADE CONSTRAINTS;
DROP TABLE JMD_REP_ADM_OPE_ITER_0_ACC CASCADE CONSTRAINTS;
DROP TABLE JMD_REP_ADM_OPE_ITER_1 CASCADE CONSTRAINTS;
DROP TABLE JMD_REP_ADM_OPE_ITER_1_ACC CASCADE CONSTRAINTS;
DROP TABLE JMD_REP_ADM_OPE_ITER_2 CASCADE CONSTRAINTS;
DROP TABLE JMD_REP_ADM_OPE_ITER_2_ACC CASCADE CONSTRAINTS;
DROP TABLE JMD_REP_ADM_OPE_ITER_3 CASCADE CONSTRAINTS;
DROP TABLE JMD_REP_ADM_OPE_ITER_3_ACC CASCADE CONSTRAINTS;
DROP TABLE JMD_REP_ADM_OPE_ITER_4 CASCADE CONSTRAINTS;
DROP TABLE JMD_REP_ADM_OPE_ITER_4_ACC CASCADE CONSTRAINTS;
DROP TABLE JMD_REP_ADM_OPE_ITER_5 CASCADE CONSTRAINTS;
DROP TABLE JMD_REP_ADM_OPE_ITER_5_ACC CASCADE CONSTRAINTS;
DROP TABLE JMD_REP_ADM_OPE_ITER_6 CASCADE CONSTRAINTS;
DROP TABLE JMD_REP_ADM_OPE_ITER_6_ACC CASCADE CONSTRAINTS;
DROP TABLE JMD_REP_ADM_OPE_OBCO_F CASCADE CONSTRAINTS;
DROP TABLE CARGAR_HOJA_DRIVER CASCADE CONSTRAINTS;
DROP TABLE TP_OFICINAS_OC_1 CASCADE CONSTRAINTS;
DROP TABLE TP_OFICINAS_OC_2 CASCADE CONSTRAINTS;
DROP TABLE BOLSA_DRIVER CASCADE CONSTRAINTS;
DROP TABLE CUENTA_PARTIDA_CENTRO CASCADE CONSTRAINTS;
DROP TABLE GRUPO_GASTOS CASCADE CONSTRAINTS;
DROP TABLE PARTIDA_CUENTA_CONTABLE CASCADE CONSTRAINTS;
DROP TABLE PARTIDA_LINEAS CASCADE CONSTRAINTS;
DROP TABLE PARTIDAS CASCADE CONSTRAINTS;
DROP TABLE OBJETO_DRIVER CASCADE CONSTRAINTS;


------------------------------------------------------------------------------------------
CREATE TABLE seguridad_permisos (
  codigo VARCHAR2(50) NOT NULL,
  nombre VARCHAR2(500) NOT NULL,
  descripcion VARCHAR2(500),
  CONSTRAINT seguridad_permisos_pk PRIMARY KEY(codigo)
);
INSERT INTO seguridad_permisos(codigo,nombre,descripcion) VALUES('INI_VER','INICIO - VER','Ver el módulo de Inicio.');
INSERT INTO seguridad_permisos(codigo,nombre,descripcion) VALUES('APR_VER','APROVISIONAMIENTO - VER','Ver el módulo de Aprovisionamiento.');
INSERT INTO seguridad_permisos(codigo,nombre,descripcion) VALUES('PAR_VER','PARAMETRIZACIÓN - VER','Ver el módulo de Parametrización.');
INSERT INTO seguridad_permisos(codigo,nombre,descripcion) VALUES('PRO_VER','PROCESOS - VER','Ver el módulo de Procesos.');
INSERT INTO seguridad_permisos(codigo,nombre,descripcion) VALUES('REP_VER','REPORTING - VER','Ver el módulo de Reporting.');
INSERT INTO seguridad_permisos(codigo,nombre,descripcion) VALUES('APR_BAL_CARGAR','APROVISIONAMIENTO - BALANCENTE - CARGAR','');
INSERT INTO seguridad_permisos(codigo,nombre,descripcion) VALUES('APR_DRI_CECO_CARGAR','APROVISIONAMIENTO - DRIVER CENTROS DE COSTOS - CARGAR','');
INSERT INTO seguridad_permisos(codigo,nombre,descripcion) VALUES('APR_DRI_CECO_CREAR','APROVISIONAMIENTO - DRIVER CENTROS DE COSTOS - CREAR','');
INSERT INTO seguridad_permisos(codigo,nombre,descripcion) VALUES('APR_DRI_CECO_EDITAR','APROVISIONAMIENTO - DRIVER CENTROS DE COSTOS - EDITAR','');
INSERT INTO seguridad_permisos(codigo,nombre,descripcion) VALUES('APR_DRI_CECO_ELIMINAR','APROVISIONAMIENTO - DRIVER CENTROS DE COSTOS - ELIMINAR','');
INSERT INTO seguridad_permisos(codigo,nombre,descripcion) VALUES('APR_DRI_OBCO_CARGAR','APROVISIONAMIENTO - DRIVER OBJETOS DE COSTOS - CARGAR','');
INSERT INTO seguridad_permisos(codigo,nombre,descripcion) VALUES('APR_DRI_OBCO_CREAR','APROVISIONAMIENTO - DRIVER OBJETOS DE COSTOS - CREAR','');
INSERT INTO seguridad_permisos(codigo,nombre,descripcion) VALUES('APR_DRI_OBCO_EDITAR','APROVISIONAMIENTO - DRIVER OBJETOS DE COSTOS - EDITAR','');
INSERT INTO seguridad_permisos(codigo,nombre,descripcion) VALUES('APR_DRI_OBCO_ELIMINAR','APROVISIONAMIENTO - DRIVER OBJETOS DE COSTOS - ELIMINAR','');
INSERT INTO seguridad_permisos(codigo,nombre,descripcion) VALUES('PAR_COPIAR','PARAMETRIZACIÓN - COPIAR','');
INSERT INTO seguridad_permisos(codigo,nombre,descripcion) VALUES('PAR_CTA_CAT_CREAR','PARAMETRIZACIÓN - CUENTAS CONTABLES - CREAR','');
INSERT INTO seguridad_permisos(codigo,nombre,descripcion) VALUES('PAR_CTA_CAT_EDITAR','PARAMETRIZACIÓN - CUENTAS CONTABLES - EDITAR','');
INSERT INTO seguridad_permisos(codigo,nombre,descripcion) VALUES('PAR_CTA_CAT_ELIMINAR','PARAMETRIZACIÓN - CUENTAS CONTABLES - ELIMINAR','');
INSERT INTO seguridad_permisos(codigo,nombre,descripcion) VALUES('PAR_CTA_CAT_CARGAR','PARAMETRIZACIÓN - CUENTAS CONTABLES - CARGAR','');
INSERT INTO seguridad_permisos(codigo,nombre,descripcion) VALUES('PAR_CTA_ASO_AGREGAR','PARAMETRIZACIÓN - CUENTAS CONTABLES - AGREGAR','');
INSERT INTO seguridad_permisos(codigo,nombre,descripcion) VALUES('PAR_CTA_ASO_QUITAR','PARAMETRIZACIÓN - CUENTAS CONTABLES - QUITAR','');
INSERT INTO seguridad_permisos(codigo,nombre,descripcion) VALUES('PAR_CTA_ASO_CARGAR','PARAMETRIZACIÓN - CUENTAS CONTABLES - CARGAR','');
INSERT INTO seguridad_permisos(codigo,nombre,descripcion) VALUES('PAR_GRU_CREAR','PARAMETRIZACIÓN - GRUPOS - CREAR','');
INSERT INTO seguridad_permisos(codigo,nombre,descripcion) VALUES('PAR_GRU_EDITAR','PARAMETRIZACIÓN - GRUPOS - EDITAR','');
INSERT INTO seguridad_permisos(codigo,nombre,descripcion) VALUES('PAR_GRU_ELIMINAR','PARAMETRIZACIÓN - GRUPOS - ELIMINAR','');
INSERT INTO seguridad_permisos(codigo,nombre,descripcion) VALUES('PAR_GRU_CARGAR','PARAMETRIZACIÓN - GRUPOS - CARGAR','');
INSERT INTO seguridad_permisos(codigo,nombre,descripcion) VALUES('PAR_GRU_CTA_ASIGNAR','PARAMETRIZACIÓN - GRUPOS A CUENTAS CONTABLES - ASIGNAR','');
INSERT INTO seguridad_permisos(codigo,nombre,descripcion) VALUES('PAR_GRU_CTA_QUITAR','PARAMETRIZACIÓN - GRUPOS A CUENTAS CONTABLES - QUITAR','');
INSERT INTO seguridad_permisos(codigo,nombre,descripcion) VALUES('PAR_GRU_CTA_CARGAR','PARAMETRIZACIÓN - GRUPOS A CUENTAS CONTABLES - CARGAR','');
INSERT INTO seguridad_permisos(codigo,nombre,descripcion) VALUES('PAR_CECO_CAT_CREAR','PARAMETRIZACIÓN - CENTROS DE COSTOS - CREAR','');
INSERT INTO seguridad_permisos(codigo,nombre,descripcion) VALUES('PAR_CECO_CAT_EDITAR','PARAMETRIZACIÓN - CENTROS DE COSTOS - EDITAR','');
INSERT INTO seguridad_permisos(codigo,nombre,descripcion) VALUES('PAR_CECO_CAT_ELIMINAR','PARAMETRIZACIÓN - CENTROS DE COSTOS - ELIMINAR','');
INSERT INTO seguridad_permisos(codigo,nombre,descripcion) VALUES('PAR_CECO_CAT_CARGAR','PARAMETRIZACIÓN - CENTROS DE COSTOS - CARGAR','');
INSERT INTO seguridad_permisos(codigo,nombre,descripcion) VALUES('PAR_CECO_ASO_AGREGAR','PARAMETRIZACIÓN - CENTROS DE COSTOS - AGREGAR','');
INSERT INTO seguridad_permisos(codigo,nombre,descripcion) VALUES('PAR_CECO_ASO_QUITAR','PARAMETRIZACIÓN - CENTROS DE COSTOS - QUITAR','');
INSERT INTO seguridad_permisos(codigo,nombre,descripcion) VALUES('PAR_CECO_ASO_CARGAR','PARAMETRIZACIÓN - CENTROS DE COSTOS - CARGAR','');
INSERT INTO seguridad_permisos(codigo,nombre,descripcion) VALUES('PAR_OFI_CAT_CREAR','PARAMETRIZACIÓN - OFICINAS - CREAR','');
INSERT INTO seguridad_permisos(codigo,nombre,descripcion) VALUES('PAR_OFI_CAT_EDITAR','PARAMETRIZACIÓN - OFICINAS - EDITAR','');
INSERT INTO seguridad_permisos(codigo,nombre,descripcion) VALUES('PAR_OFI_CAT_ELIMINAR','PARAMETRIZACIÓN - OFICINAS - ELIMINAR','');
INSERT INTO seguridad_permisos(codigo,nombre,descripcion) VALUES('PAR_OFI_CAT_CARGAR','PARAMETRIZACIÓN - OFICINAS - CARGAR','');
INSERT INTO seguridad_permisos(codigo,nombre,descripcion) VALUES('PAR_OFI_ASO_AGREGAR','PARAMETRIZACIÓN - OFICINAS - AGREGAR','');
INSERT INTO seguridad_permisos(codigo,nombre,descripcion) VALUES('PAR_OFI_ASO_QUITAR','PARAMETRIZACIÓN - OFICINAS - QUITAR','');
INSERT INTO seguridad_permisos(codigo,nombre,descripcion) VALUES('PAR_OFI_ASO_CARGAR','PARAMETRIZACIÓN - OFICINAS - CARGAR','');
INSERT INTO seguridad_permisos(codigo,nombre,descripcion) VALUES('PAR_BAN_CAT_CREAR','PARAMETRIZACIÓN - BANCAS - CREAR','');
INSERT INTO seguridad_permisos(codigo,nombre,descripcion) VALUES('PAR_BAN_CAT_EDITAR','PARAMETRIZACIÓN - BANCAS - EDITAR','');
INSERT INTO seguridad_permisos(codigo,nombre,descripcion) VALUES('PAR_BAN_CAT_ELIMINAR','PARAMETRIZACIÓN - BANCAS - ELIMINAR','');
INSERT INTO seguridad_permisos(codigo,nombre,descripcion) VALUES('PAR_BAN_CAT_CARGAR','PARAMETRIZACIÓN - BANCAS - CARGAR','');
INSERT INTO seguridad_permisos(codigo,nombre,descripcion) VALUES('PAR_BAN_ASO_AGREGAR','PARAMETRIZACIÓN - BANCAS - AGREGAR','');
INSERT INTO seguridad_permisos(codigo,nombre,descripcion) VALUES('PAR_BAN_ASO_QUITAR','PARAMETRIZACIÓN - BANCAS - QUITAR','');
INSERT INTO seguridad_permisos(codigo,nombre,descripcion) VALUES('PAR_BAN_ASO_CARGAR','PARAMETRIZACIÓN - BANCAS - CARGAR','');
INSERT INTO seguridad_permisos(codigo,nombre,descripcion) VALUES('PAR_PRO_CAT_CREAR','PARAMETRIZACIÓN - PRODUCTOS - CATÁLOGO - CREAR','');
INSERT INTO seguridad_permisos(codigo,nombre,descripcion) VALUES('PAR_PRO_CAT_EDITAR','PARAMETRIZACIÓN - PRODUCTOS - CATÁLOGO - EDITAR','');
INSERT INTO seguridad_permisos(codigo,nombre,descripcion) VALUES('PAR_PRO_CAT_ELIMINAR','PARAMETRIZACIÓN - PRODUCTOS - CATÁLOGO - ELIMINAR','');
INSERT INTO seguridad_permisos(codigo,nombre,descripcion) VALUES('PAR_PRO_CAT_CARGAR','PARAMETRIZACIÓN - PRODUCTOS - CATÁLOGO - CARGAR','');
INSERT INTO seguridad_permisos(codigo,nombre,descripcion) VALUES('PAR_PRO_ASO_AGREGAR','PARAMETRIZACIÓN - PRODUCTOS - ASOCIACIÓN - AGREGAR','');
INSERT INTO seguridad_permisos(codigo,nombre,descripcion) VALUES('PAR_PRO_ASO_QUITAR','PARAMETRIZACIÓN - PRODUCTOS - ASOCIACIÓN - QUITAR','');
INSERT INTO seguridad_permisos(codigo,nombre,descripcion) VALUES('PAR_PRO_ASO_CARGAR','PARAMETRIZACIÓN - PRODUCTOS - ASOCIACIÓN - CARGAR','');
INSERT INTO seguridad_permisos(codigo,nombre,descripcion) VALUES('PAR_DRI_ENT_ASIGNAR','PARAMETRIZACIÓN - DRIVER A ENTIDAD - ASIGNAR','');
INSERT INTO seguridad_permisos(codigo,nombre,descripcion) VALUES('PAR_DRI_ENT_QUITAR','PARAMETRIZACIÓN - DRIVER A ENTIDAD - QUITAR','');
INSERT INTO seguridad_permisos(codigo,nombre,descripcion) VALUES('PAR_DRI_ENT_CARGAR','PARAMETRIZACIÓN - DRIVER A ENTIDAD - CARGAR','');
INSERT INTO seguridad_permisos(codigo,nombre,descripcion) VALUES('PRO_EJECUTAR','PROCESOS - EJECUTAR','');
INSERT INTO seguridad_permisos(codigo,nombre,descripcion) VALUES('REP_DESCARGAR','REPORTING - DESCARGAR','');
COMMIT;
----------------------------------------------------------------------------------------------------------------------------------------------------
CREATE TABLE seguridad_roles (
  codigo VARCHAR2(10) NOT NULL,
  nombre VARCHAR2(200) NOT NULL,
  descripcion VARCHAR2(500),
  fecha_creacion DATE,
  fecha_actualizacion DATE,
  CONSTRAINT seguridad_roles_pk PRIMARY KEY(codigo)
);
INSERT INTO seguridad_roles(codigo,nombre,descripcion) VALUES('ADM','Administrador','Rol con permisos de lectura, creación, edición y eliminación.');
INSERT INTO seguridad_roles(codigo,nombre,descripcion) VALUES('USR','Usuario','Rol con permisos de lectura.');
COMMIT;
-------------------------------------------------------------------------------------------------------------------------------------------------------
CREATE TABLE seguridad_permiso_rol (
  permiso_codigo VARCHAR2(50) NOT NULL,
  rol_codigo VARCHAR2(10) NOT NULL,
  CONSTRAINT seguridad_permiso_rol_1_fk FOREIGN KEY(permiso_codigo) REFERENCES seguridad_permisos(codigo),
  CONSTRAINT seguridad_permiso_rol_2_fk FOREIGN KEY(rol_codigo) REFERENCES seguridad_roles(codigo)
);
INSERT INTO seguridad_permiso_rol(permiso_codigo,rol_codigo) VALUES('INI_VER','ADM');
INSERT INTO seguridad_permiso_rol(permiso_codigo,rol_codigo) VALUES('APR_VER','ADM');
INSERT INTO seguridad_permiso_rol(permiso_codigo,rol_codigo) VALUES('PAR_VER','ADM');
INSERT INTO seguridad_permiso_rol(permiso_codigo,rol_codigo) VALUES('PRO_VER','ADM');
INSERT INTO seguridad_permiso_rol(permiso_codigo,rol_codigo) VALUES('REP_VER','ADM');
INSERT INTO seguridad_permiso_rol(permiso_codigo,rol_codigo) VALUES('APR_BAL_CARGAR','ADM');
INSERT INTO seguridad_permiso_rol(permiso_codigo,rol_codigo) VALUES('APR_DRI_CECO_CARGAR','ADM');
INSERT INTO seguridad_permiso_rol(permiso_codigo,rol_codigo) VALUES('APR_DRI_CECO_CREAR','ADM');
INSERT INTO seguridad_permiso_rol(permiso_codigo,rol_codigo) VALUES('APR_DRI_CECO_EDITAR','ADM');
INSERT INTO seguridad_permiso_rol(permiso_codigo,rol_codigo) VALUES('APR_DRI_CECO_ELIMINAR','ADM');
INSERT INTO seguridad_permiso_rol(permiso_codigo,rol_codigo) VALUES('APR_DRI_OBCO_CARGAR','ADM');
INSERT INTO seguridad_permiso_rol(permiso_codigo,rol_codigo) VALUES('APR_DRI_OBCO_CREAR','ADM');
INSERT INTO seguridad_permiso_rol(permiso_codigo,rol_codigo) VALUES('APR_DRI_OBCO_EDITAR','ADM');
INSERT INTO seguridad_permiso_rol(permiso_codigo,rol_codigo) VALUES('APR_DRI_OBCO_ELIMINAR','ADM');
INSERT INTO seguridad_permiso_rol(permiso_codigo,rol_codigo) VALUES('PAR_COPIAR','ADM');
INSERT INTO seguridad_permiso_rol(permiso_codigo,rol_codigo) VALUES('PAR_CTA_CAT_CREAR','ADM');
INSERT INTO seguridad_permiso_rol(permiso_codigo,rol_codigo) VALUES('PAR_CTA_CAT_EDITAR','ADM');
INSERT INTO seguridad_permiso_rol(permiso_codigo,rol_codigo) VALUES('PAR_CTA_CAT_ELIMINAR','ADM');
INSERT INTO seguridad_permiso_rol(permiso_codigo,rol_codigo) VALUES('PAR_CTA_CAT_CARGAR','ADM');
INSERT INTO seguridad_permiso_rol(permiso_codigo,rol_codigo) VALUES('PAR_CTA_ASO_AGREGAR','ADM');
INSERT INTO seguridad_permiso_rol(permiso_codigo,rol_codigo) VALUES('PAR_CTA_ASO_QUITAR','ADM');
INSERT INTO seguridad_permiso_rol(permiso_codigo,rol_codigo) VALUES('PAR_CTA_ASO_CARGAR','ADM');
INSERT INTO seguridad_permiso_rol(permiso_codigo,rol_codigo) VALUES('PAR_GRU_CREAR','ADM');
INSERT INTO seguridad_permiso_rol(permiso_codigo,rol_codigo) VALUES('PAR_GRU_EDITAR','ADM');
INSERT INTO seguridad_permiso_rol(permiso_codigo,rol_codigo) VALUES('PAR_GRU_ELIMINAR','ADM');
INSERT INTO seguridad_permiso_rol(permiso_codigo,rol_codigo) VALUES('PAR_GRU_CARGAR','ADM');
INSERT INTO seguridad_permiso_rol(permiso_codigo,rol_codigo) VALUES('PAR_GRU_CTA_ASIGNAR','ADM');
INSERT INTO seguridad_permiso_rol(permiso_codigo,rol_codigo) VALUES('PAR_GRU_CTA_QUITAR','ADM');
INSERT INTO seguridad_permiso_rol(permiso_codigo,rol_codigo) VALUES('PAR_GRU_CTA_CARGAR','ADM');
INSERT INTO seguridad_permiso_rol(permiso_codigo,rol_codigo) VALUES('PAR_CECO_CAT_CREAR','ADM');
INSERT INTO seguridad_permiso_rol(permiso_codigo,rol_codigo) VALUES('PAR_CECO_CAT_EDITAR','ADM');
INSERT INTO seguridad_permiso_rol(permiso_codigo,rol_codigo) VALUES('PAR_CECO_CAT_ELIMINAR','ADM');
INSERT INTO seguridad_permiso_rol(permiso_codigo,rol_codigo) VALUES('PAR_CECO_CAT_CARGAR','ADM');
INSERT INTO seguridad_permiso_rol(permiso_codigo,rol_codigo) VALUES('PAR_CECO_ASO_AGREGAR','ADM');
INSERT INTO seguridad_permiso_rol(permiso_codigo,rol_codigo) VALUES('PAR_CECO_ASO_QUITAR','ADM');
INSERT INTO seguridad_permiso_rol(permiso_codigo,rol_codigo) VALUES('PAR_CECO_ASO_CARGAR','ADM');
INSERT INTO seguridad_permiso_rol(permiso_codigo,rol_codigo) VALUES('PAR_OFI_CAT_CREAR','ADM');
INSERT INTO seguridad_permiso_rol(permiso_codigo,rol_codigo) VALUES('PAR_OFI_CAT_EDITAR','ADM');
INSERT INTO seguridad_permiso_rol(permiso_codigo,rol_codigo) VALUES('PAR_OFI_CAT_ELIMINAR','ADM');
INSERT INTO seguridad_permiso_rol(permiso_codigo,rol_codigo) VALUES('PAR_OFI_CAT_CARGAR','ADM');
INSERT INTO seguridad_permiso_rol(permiso_codigo,rol_codigo) VALUES('PAR_OFI_ASO_AGREGAR','ADM');
INSERT INTO seguridad_permiso_rol(permiso_codigo,rol_codigo) VALUES('PAR_OFI_ASO_QUITAR','ADM');
INSERT INTO seguridad_permiso_rol(permiso_codigo,rol_codigo) VALUES('PAR_OFI_ASO_CARGAR','ADM');
INSERT INTO seguridad_permiso_rol(permiso_codigo,rol_codigo) VALUES('PAR_BAN_CAT_CREAR','ADM');
INSERT INTO seguridad_permiso_rol(permiso_codigo,rol_codigo) VALUES('PAR_BAN_CAT_EDITAR','ADM');
INSERT INTO seguridad_permiso_rol(permiso_codigo,rol_codigo) VALUES('PAR_BAN_CAT_ELIMINAR','ADM');
INSERT INTO seguridad_permiso_rol(permiso_codigo,rol_codigo) VALUES('PAR_BAN_CAT_CARGAR','ADM');
INSERT INTO seguridad_permiso_rol(permiso_codigo,rol_codigo) VALUES('PAR_BAN_ASO_AGREGAR','ADM');
INSERT INTO seguridad_permiso_rol(permiso_codigo,rol_codigo) VALUES('PAR_BAN_ASO_QUITAR','ADM');
INSERT INTO seguridad_permiso_rol(permiso_codigo,rol_codigo) VALUES('PAR_BAN_ASO_CARGAR','ADM');
INSERT INTO seguridad_permiso_rol(permiso_codigo,rol_codigo) VALUES('PAR_PRO_CAT_CREAR','ADM');
INSERT INTO seguridad_permiso_rol(permiso_codigo,rol_codigo) VALUES('PAR_PRO_CAT_EDITAR','ADM');
INSERT INTO seguridad_permiso_rol(permiso_codigo,rol_codigo) VALUES('PAR_PRO_CAT_ELIMINAR','ADM');
INSERT INTO seguridad_permiso_rol(permiso_codigo,rol_codigo) VALUES('PAR_PRO_CAT_CARGAR','ADM');
INSERT INTO seguridad_permiso_rol(permiso_codigo,rol_codigo) VALUES('PAR_PRO_ASO_AGREGAR','ADM');
INSERT INTO seguridad_permiso_rol(permiso_codigo,rol_codigo) VALUES('PAR_PRO_ASO_QUITAR','ADM');
INSERT INTO seguridad_permiso_rol(permiso_codigo,rol_codigo) VALUES('PAR_PRO_ASO_CARGAR','ADM');
INSERT INTO seguridad_permiso_rol(permiso_codigo,rol_codigo) VALUES('PAR_DRI_ENT_ASIGNAR','ADM');
INSERT INTO seguridad_permiso_rol(permiso_codigo,rol_codigo) VALUES('PAR_DRI_ENT_QUITAR','ADM');
INSERT INTO seguridad_permiso_rol(permiso_codigo,rol_codigo) VALUES('PAR_DRI_ENT_CARGAR','ADM');
INSERT INTO seguridad_permiso_rol(permiso_codigo,rol_codigo) VALUES('PRO_EJECUTAR','ADM');
INSERT INTO seguridad_permiso_rol(permiso_codigo,rol_codigo) VALUES('REP_DESCARGAR','ADM');
INSERT INTO seguridad_permiso_rol(permiso_codigo,rol_codigo) VALUES('INI_VER','USR');
INSERT INTO seguridad_permiso_rol(permiso_codigo,rol_codigo) VALUES('APR_VER','USR');
INSERT INTO seguridad_permiso_rol(permiso_codigo,rol_codigo) VALUES('PAR_VER','USR');
INSERT INTO seguridad_permiso_rol(permiso_codigo,rol_codigo) VALUES('PRO_VER','USR');
INSERT INTO seguridad_permiso_rol(permiso_codigo,rol_codigo) VALUES('REP_VER','USR');
COMMIT;
-----------------------------------------------------------------------------------------------------------------------------------------
CREATE TABLE seguridad_usuarios (
  usuario VARCHAR2(200) NOT NULL,
  contrasenha VARCHAR2(200) NOT NULL,
  nombres VARCHAR2(200),
  apellidos VARCHAR2(200),
  rol_codigo VARCHAR2(10) NOT NULL,
  CONSTRAINT seguridad_usuarios_pk PRIMARY KEY (usuario),
  CONSTRAINT seguridad_usuarios_1_fk FOREIGN KEY (rol_codigo) REFERENCES seguridad_roles(codigo)
);
INSERT INTO seguridad_usuarios(usuario,contrasenha,rol_codigo) VALUES('admin','secret','ADM');
INSERT INTO seguridad_usuarios(usuario,contrasenha,rol_codigo) VALUES('user','secret','USR');
COMMIT;
------------------------------------------------------------------------------------------
CREATE TABLE plan_de_cuentas (
  codigo VARCHAR2(10) NOT NULL,
  nombre VARCHAR2(100) NOT NULL,
  esta_activo NUMBER(1) NULL,
  reparto_tipo NUMBER(1) NOT NULL,
  atribuible CHAR(2 BYTE) NOT NULL ,
	tipo CHAR(2 BYTE) NOT NULL ,
	clase CHAR(2 BYTE) NOT NULL ,
  fecha_creacion DATE,
  fecha_actualizacion DATE,
  CONSTRAINT plan_de_cuentas_pk PRIMARY KEY(codigo)
);
------------------------------------------------------------------------------------------
CREATE TABLE plan_de_cuenta_lineas (
  plan_de_cuenta_codigo VARCHAR2(10) NOT NULL,
  periodo NUMBER(6) NOT NULL,
  saldo NUMBER(35,8) NOT NULL,
  fecha_creacion DATE,
  fecha_actualizacion DATE,
  CONSTRAINT plan_de_cuenta_lineas_pk PRIMARY KEY(plan_de_cuenta_codigo,periodo)
);
------------------------------------------------------------------------------------------
CREATE TABLE centro_niveles (
  codigo VARCHAR2(10) NOT NULL,
  nombre VARCHAR2(200) NOT NULL,
  descripcion VARCHAR2(500) NULL,
  fecha_creacion DATE,
  fecha_actualizacion DATE,
  CONSTRAINT centro_niveles_pk PRIMARY KEY(codigo)
);

INSERT INTO CENTRO_NIVELES(CODIGO,NOMBRE,DESCRIPCION,FECHA_CREACION,FECHA_ACTUALIZACION) VALUES ('-1','-1','CENTROS DE COSTOS TIPO BOLSA.',TO_DATE('01/01/18','DD/MM/RR'),TO_DATE('01/01/18','DD/MM/RR'));
INSERT INTO CENTRO_NIVELES(CODIGO,NOMBRE,DESCRIPCION,FECHA_CREACION,FECHA_ACTUALIZACION) VALUES ('-','TODOS','CENTROS DE COSTOS DE TODOS LOS NIVELES.',TO_DATE('01/01/18','DD/MM/RR'),TO_DATE('01/01/18','DD/MM/RR'));
INSERT INTO CENTRO_NIVELES(CODIGO,NOMBRE,DESCRIPCION,FECHA_CREACION,FECHA_ACTUALIZACION) VALUES ('1','1','CENTROS DE COSTOS DE NIVEL 1.',TO_DATE('01/01/18','DD/MM/RR'),TO_DATE('01/01/18','DD/MM/RR'));
INSERT INTO CENTRO_NIVELES(CODIGO,NOMBRE,DESCRIPCION,FECHA_CREACION,FECHA_ACTUALIZACION) VALUES ('2','2','CENTROS DE COSTOS DE NIVEL 2.',TO_DATE('01/01/18','DD/MM/RR'),TO_DATE('01/01/18','DD/MM/RR'));
INSERT INTO CENTRO_NIVELES(CODIGO,NOMBRE,DESCRIPCION,FECHA_CREACION,FECHA_ACTUALIZACION) VALUES ('3','3','CENTROS DE COSTOS DE NIVEL 3.',TO_DATE('01/01/18','DD/MM/RR'),TO_DATE('01/01/18','DD/MM/RR'));
INSERT INTO CENTRO_NIVELES(CODIGO,NOMBRE,DESCRIPCION,FECHA_CREACION,FECHA_ACTUALIZACION) VALUES ('4','4','CENTROS DE COSTOS DE NIVEL 4.',TO_DATE('01/01/18','DD/MM/RR'),TO_DATE('01/01/18','DD/MM/RR'));
INSERT INTO CENTRO_NIVELES(CODIGO,NOMBRE,DESCRIPCION,FECHA_CREACION,FECHA_ACTUALIZACION) VALUES ('5','5','CENTROS DE COSTOS DE NIVEL 5.',TO_DATE('01/01/18','DD/MM/RR'),TO_DATE('01/01/18','DD/MM/RR'));
INSERT INTO CENTRO_NIVELES(CODIGO,NOMBRE,DESCRIPCION,FECHA_CREACION,FECHA_ACTUALIZACION) VALUES ('6','6','CENTROS DE COSTOS DE NIVEL 6.',TO_DATE('01/01/18','DD/MM/RR'),TO_DATE('01/01/18','DD/MM/RR'));
INSERT INTO CENTRO_NIVELES(CODIGO,NOMBRE,DESCRIPCION,FECHA_CREACION,FECHA_ACTUALIZACION) VALUES ('0','0','CENTROS DE COSTOS DE NIVEL 0.',TO_DATE('01/01/18','DD/MM/RR'),TO_DATE('01/01/18','DD/MM/RR'));

COMMIT;

--SELECT * FROM centro_niveles;
------------------------------------------------------------------------------------------
CREATE TABLE centro_tipos (
  codigo VARCHAR2(10) NOT NULL,
  nombre VARCHAR2(200) NOT NULL,
  descripcion VARCHAR2(500) NULL,
  fecha_creacion DATE,
  fecha_actualizacion DATE,
  CONSTRAINT centro_tipos_pk PRIMARY KEY(codigo)
);

INSERT INTO CENTRO_TIPOS (CODIGO,NOMBRE,DESCRIPCION,FECHA_CREACION,FECHA_ACTUALIZACION) VALUES ('-','TODOS','TODOS LOS GRUPOS.',TO_DATE('01/01/18','DD/MM/RR'),TO_DATE('01/01/18','DD/MM/RR'));
INSERT INTO CENTRO_TIPOS (CODIGO,NOMBRE,DESCRIPCION,FECHA_CREACION,FECHA_ACTUALIZACION) VALUES ('A','STAFF','CENTROS DE COSTOS CORRESPONDIENTES A LOS GASTOS DE STAFF.',TO_DATE('01/01/18','DD/MM/RR'),TO_DATE('01/01/18','DD/MM/RR'));
INSERT INTO CENTRO_TIPOS (CODIGO,NOMBRE,DESCRIPCION,FECHA_CREACION,FECHA_ACTUALIZACION) VALUES ('B','SOPORTE','CENTROS DE COSTOS CORRESPONDIENTES A LOS GASTOS DE SOPORTE.',TO_DATE('01/01/18','DD/MM/RR'),TO_DATE('01/01/18','DD/MM/RR'));
INSERT INTO CENTRO_TIPOS (CODIGO,NOMBRE,DESCRIPCION,FECHA_CREACION,FECHA_ACTUALIZACION) VALUES ('C','-','CENTROS DE COSTOS DE APOYO FICTICIO.',TO_DATE('01/01/18','DD/MM/RR'),TO_DATE('01/01/18','DD/MM/RR'));
INSERT INTO CENTRO_TIPOS (CODIGO,NOMBRE,DESCRIPCION,FECHA_CREACION,FECHA_ACTUALIZACION) VALUES ('D','FICTICIO','CENTROS DE COSTOS DE APOYO FICTICIO.',TO_DATE('01/01/18','DD/MM/RR'),TO_DATE('01/01/18','DD/MM/RR'));
INSERT INTO CENTRO_TIPOS (CODIGO,NOMBRE,DESCRIPCION,FECHA_CREACION,FECHA_ACTUALIZACION) VALUES ('E','CANALES','CENTROS DE COSTOS OBJETO CANAL.',TO_DATE('01/01/18','DD/MM/RR'),TO_DATE('01/01/18','DD/MM/RR'));
INSERT INTO CENTRO_TIPOS (CODIGO,NOMBRE,DESCRIPCION,FECHA_CREACION,FECHA_ACTUALIZACION) VALUES ('F','LINEAS DE NEGOCIO','CENTROS DE COSTOS OBJETO LINEA DE NEGOCIO.',TO_DATE('01/01/18','DD/MM/RR'),TO_DATE('01/01/18','DD/MM/RR'));

--SELECT * FROM centro_tipos;
------------------------------------------------------------------------------------------
CREATE TABLE centros (
  codigo VARCHAR2(10) NOT NULL,
  nombre VARCHAR2(200) NOT NULL,
  esta_activo NUMBER(1) NULL,
  nivel NUMBER(10) NOT NULL,
  centro_padre_codigo VARCHAR2(10) NULL,
  centro_tipo_codigo VARCHAR2(10) NOT NULL,
  fecha_creacion DATE,
  fecha_actualizacion DATE,
  oficina_codigo VARCHAR2(10) NULL,
  reparto_tipo NUMBER(1) NOT NULL,
  CONSTRAINT centros_pk PRIMARY KEY(codigo)
);
------------------------------------------------------------------------------------------
CREATE TABLE centro_lineas (
  centro_codigo VARCHAR2(10) NOT NULL,
  periodo NUMBER(6) NOT NULL,
  iteracion NUMBER(10) NOT NULL,
  saldo NUMBER(35,8) NOT NULL,
  entidad_origen_codigo VARCHAR2(10) NOT NULL,
  grupo_gasto CHAR(2),
  fecha_creacion DATE,
  fecha_actualizacion DATE
);
------------------------------------------------------------------------------------------
CREATE TABLE entidad_tipos (
  codigo VARCHAR2(10) NOT NULL,
  nombre VARCHAR2(200) NOT NULL,
  descripcion VARCHAR2(500) NULL,
  CONSTRAINT entidad_tipos_pk PRIMARY KEY(codigo)
);

INSERT INTO entidad_tipos(codigo,nombre) VALUES('-','Todos');
INSERT INTO entidad_tipos(codigo,nombre) VALUES('CTA','Cuenta Contable');
INSERT INTO entidad_tipos(codigo,nombre) VALUES('GCTA','Grupo de Cuentas Contables');
INSERT INTO entidad_tipos(codigo,nombre) VALUES('CECO','Centro de Costos');
INSERT INTO entidad_tipos(codigo,nombre) VALUES('BAN','Banca');
INSERT INTO entidad_tipos(codigo,nombre) VALUES('OFI','Oficina');
INSERT INTO entidad_tipos(codigo,nombre) VALUES('PRO','Producto');
COMMIT;
--SELECT * FROM objeto_tipos;
------------------------------------------------------------------------------------------
CREATE TABLE driver_tipos (
  codigo VARCHAR2(10) NOT NULL,
  nombre VARCHAR2(200) NOT NULL,
  descripcion VARCHAR2(500) NULL,
  CONSTRAINT driver_tipo_pk PRIMARY KEY (codigo)
);
INSERT INTO driver_tipos(codigo,nombre,descripcion) VALUES('CECO','Centros de Costos',null);
INSERT INTO driver_tipos(codigo,nombre,descripcion) VALUES('OBCO','Objetos de Costos',null);
COMMIT;
------------------------------------------------------------------------------------------
CREATE TABLE drivers (
  codigo VARCHAR2(10) NOT NULL,
  nombre VARCHAR2(200) NULL,
  descripcion VARCHAR2(500) NULL,
  driver_tipo_codigo VARCHAR2(10),
  reparto_tipo NUMBER(1) NOT NULL,
  fecha_creacion DATE,
  fecha_actualizacion DATE,
  CONSTRAINT driver_pk PRIMARY KEY (codigo),
  CONSTRAINT driver_1_fk FOREIGN KEY(driver_tipo_codigo) REFERENCES driver_tipos(codigo)
);
------------------------------------------------------------------------------------------
CREATE TABLE driver_lineas (
  driver_codigo VARCHAR2(10) NOT NULL,
  entidad_destino_codigo VARCHAR2(10) NOT NULL,
  periodo NUMBER(6) NOT NULL,
  porcentaje NUMBER(10,4) NOT NULL,
  fecha_creacion DATE,
  fecha_actualizacion DATE,
  CONSTRAINT driver_lineas_pk PRIMARY KEY(driver_codigo,entidad_destino_codigo,periodo),
  CONSTRAINT driver_lineas_1_fk FOREIGN KEY(driver_codigo) REFERENCES drivers(codigo)
);
------------------------------------------------------------------------------------------
-- se van a crear varias lineas cada mes como parte de auditoria
CREATE TABLE entidad_origen_driver (
  entidad_origen_codigo VARCHAR2(10) NOT NULL,
  driver_codigo VARCHAR2(10) NOT NULL,
  periodo NUMBER(6) NOT NULL,
  fecha_creacion DATE,
  fecha_actualizacion DATE,
  CONSTRAINT entidad_origen_driver_pk PRIMARY KEY(entidad_origen_codigo,driver_codigo,periodo),
  CONSTRAINT entidad_origen_driver_1_fk FOREIGN KEY(driver_codigo) REFERENCES drivers(codigo)
);
------------------------------------------------------------------------------------------
CREATE TABLE banca_grupos (
  codigo VARCHAR2(10) NOT NULL,
  nombre VARCHAR2(200) NOT NULL,
  nivel NUMBER(2) NOT NULL,
  esta_activo NUMBER(1) NULL,
  fecha_creacion DATE,
  fecha_actualizacion DATE,
  CONSTRAINT banca_grupos_pk PRIMARY KEY(codigo)
);
------------------------------------------------------------------------------------------
CREATE TABLE oficina_grupos (
  codigo VARCHAR2(10) NOT NULL,
  nombre VARCHAR2(200) NOT NULL,
  nivel NUMBER(2) NOT NULL,
  esta_activo NUMBER(1) NULL,
  fecha_creacion DATE,
  fecha_actualizacion DATE,
  CONSTRAINT oficina_grupos_pk PRIMARY KEY(codigo)
);
------------------------------------------------------------------------------------------
CREATE TABLE producto_grupos (
  codigo VARCHAR2(10) NOT NULL,
  nombre VARCHAR2(200) NOT NULL,
  nivel NUMBER(2) NOT NULL,
  esta_activo NUMBER(1) NULL,
  fecha_creacion DATE,
  fecha_actualizacion DATE,
  CONSTRAINT producto_grupos_pk PRIMARY KEY(codigo)
);
------------------------------------------------------------------------------------------
--OFICINA-BANCA-PRODUCTO-BAN-OFI-PRO
CREATE TABLE jerarquia (
  periodo NUMBER(6) NOT NULL,
  entidad_codigo VARCHAR2(10) NOT NULL,
  entidad_tipo VARCHAR2(10) NOT NULL,
  nivel NUMBER(2) NOT NULL,
  entidad_padre_codigo VARCHAR2(10) NOT NULL,
  CONSTRAINT jerarquia_pk PRIMARY KEY(periodo,entidad_codigo,entidad_tipo,nivel,entidad_padre_codigo)
);
------------------------------------------------------------------------------------------
CREATE TABLE productos (
  codigo VARCHAR2(10) NOT NULL,
  nombre VARCHAR2(200) NOT NULL,
  esta_activo NUMBER(1) NULL,
  fecha_creacion DATE,
  fecha_actualizacion DATE,
  CONSTRAINT productos_pk PRIMARY KEY(codigo)
);
------------------------------------------------------------------------------------------
CREATE TABLE producto_lineas (
  producto_codigo VARCHAR2(10) NOT NULL,
  periodo NUMBER(6) NOT NULL,
  fecha_creacion DATE,
  fecha_actualizacion DATE,
  CONSTRAINT producto_lineas_pk PRIMARY KEY(producto_codigo,periodo),
  CONSTRAINT producto_lineas_1_fk FOREIGN KEY(producto_codigo) REFERENCES productos(codigo)
);
--========================================================================================
CREATE TABLE bancas (
  codigo VARCHAR2(10) NOT NULL,
  nombre VARCHAR2(100) NOT NULL,
  esta_activo NUMBER(1) NULL,
  fecha_creacion DATE,
  fecha_actualizacion DATE,
  CONSTRAINT bancas_pk PRIMARY KEY(codigo)
);
------------------------------------------------------------------------------------------
CREATE TABLE banca_lineas (
  banca_codigo VARCHAR2(10) NOT NULL,
  periodo NUMBER(6) NOT NULL,
  fecha_creacion DATE,
  fecha_actualizacion DATE,
  CONSTRAINT banca_lineas_pk PRIMARY KEY(banca_codigo,periodo),
  CONSTRAINT banca_lineas_1_fk FOREIGN KEY(banca_codigo) REFERENCES bancas(codigo)
);
--========================================================================================
CREATE TABLE oficinas (
  codigo VARCHAR2(10) NOT NULL,
  nombre VARCHAR2(100) NOT NULL,
  esta_activo NUMBER(1) NULL,
  fecha_creacion DATE,
  fecha_actualizacion DATE,
  CONSTRAINT oficinas_pk PRIMARY KEY(codigo)
);
------------------------------------------------------------------------------------------
CREATE TABLE oficina_lineas (
  oficina_codigo VARCHAR2(10) NOT NULL,
  periodo NUMBER(6) NOT NULL,
  fecha_creacion DATE,
  fecha_actualizacion DATE,
  CONSTRAINT oficina_lineas_pk PRIMARY KEY(oficina_codigo,periodo),
  CONSTRAINT oficina_lineas_1_fk FOREIGN KEY(oficina_codigo) REFERENCES oficinas(codigo)
);
------------------------------------------------------------------------------------------
CREATE TABLE driver_obco_lineas (
  driver_codigo VARCHAR2(10) NOT NULL,
  banca_codigo VARCHAR2(10) NOT NULL,
  oficina_codigo VARCHAR2(10) NOT NULL,
  producto_codigo VARCHAR2(10) NOT NULL,
  periodo NUMBER(6) NOT NULL,
  porcentaje NUMBER(10,4) NOT NULL,
  fecha_creacion DATE,
  fecha_actualizacion DATE
);
------------------------------------------------------------------------------------------
CREATE TABLE obco_lineas (
  oficina_codigo VARCHAR2(10) NOT NULL,
  banca_codigo VARCHAR2(10) NOT NULL,
  producto_codigo VARCHAR2(10) NOT NULL,
  periodo NUMBER(6) NOT NULL,
  entidad_origen_codigo VARCHAR2(10) NOT NULL,
  saldo NUMBER(35,8) NOT NULL,
  reparto_tipo NUMBER(1) NOT NULL,
  fecha_creacion DATE,
  fecha_actualizacion DATE
);
------------------------------------------------------------------------------------------
CREATE TABLE ejecuciones (
  periodo NUMBER(6) NOT NULL,
  fase NUMBER(10) NOT NULL,
  reparto_tipo NUMBER(1) NOT NULL,
  fecha_ini DATE NOT NULL,
  fecha_fin DATE NULL
);
--SELECT * FROM ejecuciones;
------------------------------------------------------------------------------------------
CREATE TABLE JMD_REP_GRUPO_SALDO (
  PERIODO NUMBER(6) NOT NULL,
  GRUPO_CODIGO VARCHAR2(10) NOT NULL,
  SALDO NUMBER(35,8) NOT NULL
);
------------------------------------------------------------------------------------------
CREATE TABLE JMD_REP_ADM_OPE_M (
  PERIODO NUMBER(6) NOT NULL,
  CENTRO_CODIGO VARCHAR2(10) NOT NULL,
  CENTRO_NIVEL NUMBER(10) NOT NULL
);
CREATE TABLE JMD_REP_ADM_OPE_ITER_0_ACC (
  PERIODO NUMBER(6) NOT NULL,
  CENTRO_CODIGO VARCHAR2(10) NOT NULL,
  CENTRO_NIVEL NUMBER(10) NOT NULL,
  GASTOS_OPERATIVOS NUMBER,
  GASTOS_ADMINISTRATIVOS NUMBER,
  GASTOS_TOTALES NUMBER
);
CREATE TABLE JMD_REP_ADM_OPE_ITER_1 (
  PERIODO NUMBER(6) NOT NULL,
  CENTRO_CODIGO VARCHAR2(10) NOT NULL,
  GASTOS_OPERATIVOS NUMBER,
  GASTOS_ADMINISTRATIVOS NUMBER,
  GASTOS_TOTALES NUMBER
);
CREATE TABLE JMD_REP_ADM_OPE_ITER_1_ACC (
  PERIODO NUMBER(6) NOT NULL,
  CENTRO_CODIGO VARCHAR2(10) NOT NULL,
  CENTRO_NIVEL NUMBER(10) NOT NULL,
  GASTOS_OPERATIVOS NUMBER,
  GASTOS_ADMINISTRATIVOS NUMBER,
  GASTOS_TOTALES NUMBER
);
CREATE TABLE JMD_REP_ADM_OPE_ITER_2 (
  PERIODO NUMBER(6) NOT NULL,
  CENTRO_CODIGO VARCHAR2(10) NOT NULL,
  GASTOS_OPERATIVOS NUMBER,
  GASTOS_ADMINISTRATIVOS NUMBER,
  GASTOS_TOTALES NUMBER
);
CREATE TABLE JMD_REP_ADM_OPE_ITER_2_ACC (
  PERIODO NUMBER(6) NOT NULL,
  CENTRO_CODIGO VARCHAR2(10) NOT NULL,
  CENTRO_NIVEL NUMBER(10) NOT NULL,
  GASTOS_OPERATIVOS NUMBER,
  GASTOS_ADMINISTRATIVOS NUMBER,
  GASTOS_TOTALES NUMBER
);
CREATE TABLE JMD_REP_ADM_OPE_ITER_3 (
  PERIODO NUMBER(6) NOT NULL,
  CENTRO_CODIGO VARCHAR2(10) NOT NULL,
  GASTOS_OPERATIVOS NUMBER,
  GASTOS_ADMINISTRATIVOS NUMBER,
  GASTOS_TOTALES NUMBER
);
CREATE TABLE JMD_REP_ADM_OPE_ITER_3_ACC (
  PERIODO NUMBER(6) NOT NULL,
  CENTRO_CODIGO VARCHAR2(10) NOT NULL,
  CENTRO_NIVEL NUMBER(10) NOT NULL,
  GASTOS_OPERATIVOS NUMBER,
  GASTOS_ADMINISTRATIVOS NUMBER,
  GASTOS_TOTALES NUMBER
);
CREATE TABLE JMD_REP_ADM_OPE_ITER_4 (
  PERIODO NUMBER(6) NOT NULL,
  CENTRO_CODIGO VARCHAR2(10) NOT NULL,
  GASTOS_OPERATIVOS NUMBER,
  GASTOS_ADMINISTRATIVOS NUMBER,
  GASTOS_TOTALES NUMBER
);
CREATE TABLE JMD_REP_ADM_OPE_ITER_4_ACC (
  PERIODO NUMBER(6) NOT NULL,
  CENTRO_CODIGO VARCHAR2(10) NOT NULL,
  CENTRO_NIVEL NUMBER(10) NOT NULL,
  GASTOS_OPERATIVOS NUMBER,
  GASTOS_ADMINISTRATIVOS NUMBER,
  GASTOS_TOTALES NUMBER
);
CREATE TABLE JMD_REP_ADM_OPE_ITER_5 (
  PERIODO NUMBER(6) NOT NULL,
  CENTRO_CODIGO VARCHAR2(10) NOT NULL,
  GASTOS_OPERATIVOS NUMBER,
  GASTOS_ADMINISTRATIVOS NUMBER,
  GASTOS_TOTALES NUMBER
);
CREATE TABLE JMD_REP_ADM_OPE_ITER_5_ACC (
  PERIODO NUMBER(6) NOT NULL,
  CENTRO_CODIGO VARCHAR2(10) NOT NULL,
  CENTRO_NIVEL NUMBER(10) NOT NULL,
  GASTOS_OPERATIVOS NUMBER,
  GASTOS_ADMINISTRATIVOS NUMBER,
  GASTOS_TOTALES NUMBER
);
CREATE TABLE JMD_REP_ADM_OPE_ITER_6 (
  PERIODO NUMBER(6) NOT NULL,
  CENTRO_CODIGO VARCHAR2(10) NOT NULL,
  GASTOS_OPERATIVOS NUMBER,
  GASTOS_ADMINISTRATIVOS NUMBER,
  GASTOS_TOTALES NUMBER
);
CREATE TABLE JMD_REP_ADM_OPE_ITER_6_ACC (
  PERIODO NUMBER(6) NOT NULL,
  CENTRO_CODIGO VARCHAR2(10) NOT NULL,
  CENTRO_NIVEL NUMBER(10) NOT NULL,
  GASTOS_OPERATIVOS NUMBER,
  GASTOS_ADMINISTRATIVOS NUMBER,
  GASTOS_TOTALES NUMBER
);
CREATE TABLE JMD_REP_ADM_OPE_OBCO_F (
  PERIODO NUMBER(6) NOT NULL,
  OFICINA_CODIGO VARCHAR2(10) NOT NULL,
  OFICINA_NOMBRE VARCHAR2(200) NOT NULL,
  BANCA_CODIGO VARCHAR2(10) NOT NULL,
  BANCA_NOMBRE VARCHAR2(200) NOT NULL,
  PRODUCTO_CODIGO VARCHAR2(10) NOT NULL,
  PRODUCTO_NOMBRE VARCHAR2(200) NOT NULL,
  GASTOS_OPERATIVOS NUMBER,
  GASTOS_ADMINISTRATIVOS NUMBER,
  GASTOS_TOTALES NUMBER(35,8) NOT NULL,
  CECO_ORIGEN_CODIGO VARCHAR2(10) NOT NULL,
  CECO_ORIGEN_NOMBRE VARCHAR2(200) NOT NULL,
  DRIVER_CODIGO VARCHAR2(10) NOT NULL,
  DRIVER_NOMBRE VARCHAR2(200)
);
CREATE TABLE CARGAR_HOJA_DRIVER (
  EXCEL_FILA NUMBER(6) NOT NULL,
  DRIVER_CODIGO VARCHAR2(200) NOT NULL,
  CODIGO_1 VARCHAR2(200) NOT NULL,
  CODIGO_2 VARCHAR2(200) NULL,
  CODIGO_3 VARCHAR2(200) NULL,
  PORCENTAJE NUMBER(10,4) NOT NULL
);
CREATE TABLE TP_OFICINAS_OC_1 (
OFICINA_CODIGO VARCHAR2(10) NOT NULL,
OFICINA_NOMBRE VARCHAR2(200) NOT NULL,
INGRESOS_OPE_DE_CAMBIO NUMBER,
INGRESOS_GIROS NUMBER,
GASTOS_OPE_DE_CAMBIO NUMBER
);
CREATE TABLE TP_OFICINAS_OC_2 (
OFICINA_CODIGO VARCHAR2(10) NOT NULL,
OFICINA_NOMBRE VARCHAR2(200) NOT NULL,
INGRESOS_OPE_DE_CAMBIO NUMBER,
INGRESOS_GIROS NUMBER,
GASTOS_OPE_DE_CAMBIO NUMBER,
GASTOS_OPE_DE_CAMBIO_AJUSTADO NUMBER
);

CREATE TABLE BOLSA_DRIVER
   (	DRIVER_CODIGO VARCHAR2(10 BYTE) NOT NULL ,
	CUENTA_CONTABLE_CODIGO VARCHAR2(15 BYTE) NOT NULL ,
	PARTIDA_CODIGO VARCHAR2(10 BYTE) NOT NULL ,
	CENTRO_CODIGO VARCHAR2(8 BYTE) NOT NULL ,
	PERIODO NUMBER(6,0) NOT NULL ,
	FECHA_CREACION DATE,
	FECHA_ACTUALIZACION DATE
);

CREATE TABLE CUENTA_PARTIDA_CENTRO
   (	CUENTA_CONTABLE_CODIGO VARCHAR2(15 BYTE) NOT NULL ,
	PARTIDA_CODIGO VARCHAR2(10 BYTE) NOT NULL ,
	CENTRO_CODIGO VARCHAR2(8 BYTE) NOT NULL ,
	PERIODO NUMBER(6,0) NOT NULL ,
	SALDO NUMBER(35,8) NOT NULL ,
	FECHA_CREACION DATE,
	FECHA_ACTUALIZACION DATE,
	 CONSTRAINT CUENTA_PARTIDA_CENTRO_PK PRIMARY KEY (CUENTA_CONTABLE_CODIGO, PARTIDA_CODIGO, CENTRO_CODIGO)
 );

CREATE TABLE GRUPO_GASTOS (
  CODIGO VARCHAR2(8 BYTE) NOT NULL ,
 	NOMBRE VARCHAR2(100 BYTE) NOT NULL ,
 	DESCRIPCION VARCHAR2(100 BYTE),
 	FECHA_CREACION DATE,
 	FECHA_ACTUALIZACION DATE
);

INSERT INTO GRUPO_GASTOS (CODIGO,NOMBRE,DESCRIPCION,FECHA_CREACION,FECHA_ACTUALIZACION) VALUES ('GP','GASTO DE PERSONAL','GASTO DE PERSONAL',NULL,NULL);
INSERT INTO GRUPO_GASTOS (CODIGO,NOMBRE,DESCRIPCION,FECHA_CREACION,FECHA_ACTUALIZACION) VALUES ('GT','GASTO DE TECNOLOGIA','GASTO DE TECNOLOGIA',NULL,NULL);
INSERT INTO GRUPO_GASTOS (CODIGO,NOMBRE,DESCRIPCION,FECHA_CREACION,FECHA_ACTUALIZACION) VALUES ('GO','GASTO DE OPERACIONES','GASTO DE OPERACIONES',NULL,NULL);
COMMIT;

CREATE TABLE PARTIDA_CUENTA_CONTABLE
   (	PARTIDA_CODIGO VARCHAR2(10 BYTE) NOT NULL ,
	CUENTA_CONTABLE_CODIGO VARCHAR2(15 BYTE) NOT NULL ,
	PERIODO NUMBER(6,0),
	FECHA_CREACION DATE,
	FECHA_ACTUALIZACION DATE,
	SALDO NUMBER(35,8) NOT NULL,
  ES_BOLSA CHAR(2) NOT NULL
);

CREATE TABLE PARTIDA_LINEAS
   (	PARTIDA_CODIGO VARCHAR2(10 BYTE) NOT NULL ,
	PERIODO NUMBER(6,0) NOT NULL ,
	SALDO NUMBER(35,8) NOT NULL ,
	FECHA_CREACION DATE,
	FECHA_ACTUALIZACION DATE
 );

 CREATE TABLE PARTIDAS
    (	CODIGO VARCHAR2(10 BYTE) NOT NULL ,
 	NOMBRE VARCHAR2(100 BYTE) NOT NULL ,
 	REPARTO_TIPO NUMBER(1,0) NOT NULL ,
 	FECHA_CREACION DATE,
 	FECHA_ACTUALIZACION DATE,
 	GRUPO_GASTO CHAR(2 BYTE) NOT NULL
 );

 CREATE TABLE OBJETO_DRIVER
 (
   CENTRO_CODIGO VARCHAR2(8) NOT NULL
 , GRUPO_GASTO VARCHAR2(2) NOT NULL
 , DRIVER_CODIGO VARCHAR2(8) NOT NULL
 , PERIODO NUMBER(6, 0) NOT NULL
 , FECHA_CREACION DATE
 , FECHA_ACTUALIZACION DATE
 );

 CREATE TABLE SUBCANALES
(	CODIGO VARCHAR2(10 BYTE) NOT NULL ,
	NOMBRE VARCHAR2(100 BYTE) NOT NULL ,
	ESTA_ACTIVO NUMBER(1,0),
	FECHA_CREACION DATE,
	FECHA_ACTUALIZACION DATE
);

CREATE TABLE SUBCANAL_LINEAS
(	SUBCANAL_CODIGO VARCHAR2(10 BYTE) NOT NULL,
	PERIODO NUMBER(6,0) NOT NULL ,
	FECHA_CREACION DATE,
	FECHA_ACTUALIZACION DATE
);

CREATE TABLE SUBCANAL_GRUPOS
  (	CODIGO VARCHAR2(10 BYTE) NOT NULL ,
 NOMBRE VARCHAR2(100 BYTE) NOT NULL ,
 NIVEL NUMBER(2,0) NOT NULL ,
 ESTA_ACTIVO NUMBER(1,0),
 FECHA_CREACION DATE,
 FECHA_ACTUALIZACION DATE
);

INSERT INTO ENTIDAD_TIPOS (CODIGO, NOMBRE) VALUES ('SCA', 'Subcanal');

CREATE TABLE DRIVER_OBJETO_LINEAS
   (	DRIVER_CODIGO VARCHAR2(10 BYTE) NOT NULL ,
	PRODUCTO_CODIGO VARCHAR2(10 BYTE) NOT NULL ENABLE,
	SUBCANAL_CODIGO VARCHAR2(10 BYTE) NOT NULL ENABLE,
	PERIODO NUMBER(6,0) NOT NULL ENABLE,
	PORCENTAJE NUMBER(10,4) NOT NULL ENABLE,
	FECHA_CREACION DATE,
	FECHA_ACTUALIZACION DATE
);
