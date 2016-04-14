-- phpMyAdmin SQL Dump
-- version 4.5.1
-- http://www.phpmyadmin.net
--
-- Servidor: 127.0.0.1
-- Tiempo de generación: 14-04-2016 a las 07:00:37
-- Versión del servidor: 10.1.10-MariaDB
-- Versión de PHP: 7.0.4

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Base de datos: `baseriv`
--

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `carasclasificador`
--

CREATE TABLE `carasclasificador` (
  `orden` mediumint(9) NOT NULL,
  `legajo` mediumint(9) NOT NULL,
  `imagen` longblob
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `carasdetectadas`
--

CREATE TABLE `carasdetectadas` (
  `numero` mediumint(9) NOT NULL,
  `legajo` mediumint(9) DEFAULT NULL,
  `imagen` longblob,
  `fecha` datetime DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `personas`
--

CREATE TABLE `personas` (
  `legajo` mediumint(9) NOT NULL,
  `apellido` varchar(50) DEFAULT NULL,
  `nombre` varchar(50) DEFAULT NULL,
  `email` varchar(50) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Índices para tablas volcadas
--

--
-- Indices de la tabla `carasclasificador`
--
ALTER TABLE `carasclasificador`
  ADD PRIMARY KEY (`orden`,`legajo`),
  ADD KEY `legajo` (`legajo`);

--
-- Indices de la tabla `carasdetectadas`
--
ALTER TABLE `carasdetectadas`
  ADD PRIMARY KEY (`numero`),
  ADD KEY `legajo` (`legajo`);

--
-- Indices de la tabla `personas`
--
ALTER TABLE `personas`
  ADD PRIMARY KEY (`legajo`);

--
-- AUTO_INCREMENT de las tablas volcadas
--

--
-- AUTO_INCREMENT de la tabla `carasclasificador`
--
ALTER TABLE `carasclasificador`
  MODIFY `orden` mediumint(9) NOT NULL AUTO_INCREMENT;
--
-- AUTO_INCREMENT de la tabla `carasdetectadas`
--
ALTER TABLE `carasdetectadas`
  MODIFY `numero` mediumint(9) NOT NULL AUTO_INCREMENT;
--
-- AUTO_INCREMENT de la tabla `personas`
--
ALTER TABLE `personas`
  MODIFY `legajo` mediumint(9) NOT NULL AUTO_INCREMENT;
--
-- Restricciones para tablas volcadas
--

--
-- Filtros para la tabla `carasclasificador`
--
ALTER TABLE `carasclasificador`
  ADD CONSTRAINT `carasclasificador_ibfk_1` FOREIGN KEY (`legajo`) REFERENCES `personas` (`legajo`);

--
-- Filtros para la tabla `carasdetectadas`
--
ALTER TABLE `carasdetectadas`
  ADD CONSTRAINT `carasdetectadas_ibfk_1` FOREIGN KEY (`legajo`) REFERENCES `personas` (`legajo`);

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
