package ar.edu.utn.frbb.tup.service;

import ar.edu.utn.frbb.tup.model.Cuenta;
import ar.edu.utn.frbb.tup.model.TipoCuenta;
import ar.edu.utn.frbb.tup.model.TipoMoneda;
import ar.edu.utn.frbb.tup.model.exception.CuentaAlreadyExistsException;
import ar.edu.utn.frbb.tup.model.exception.TipoCuentaAlreadyExistsException;
import ar.edu.utn.frbb.tup.persistence.CuentaDao;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class CuentaServiceTest {

    @Mock
    private CuentaDao cuentaDao;

    @Mock
    private ClienteService clienteService;

    @InjectMocks
    private CuentaService cuentaService;

    @BeforeAll
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testDarDeAltaCuentaExistente() {
        Cuenta cuentaExistente = new Cuenta();
        cuentaExistente.setNumeroCuenta(123456);

        when(cuentaDao.find(123456)).thenReturn(cuentaExistente);

        Cuenta nuevaCuenta = new Cuenta();
        nuevaCuenta.setNumeroCuenta(123456);

        assertThrows(CuentaAlreadyExistsException.class, () -> cuentaService.darDeAltaCuenta(nuevaCuenta, 29857643));
    }

    @Test
    public void testDarDeAltaCuentaNoSoportada() {
        Cuenta cuentaNoSoportada = new Cuenta();
        cuentaNoSoportada.setNumeroCuenta(123456);
        cuentaNoSoportada.setTipoCuenta(TipoCuenta.CUENTA_CORRIENTE);
        cuentaNoSoportada.setMoneda(TipoMoneda.DOLARES);

        when(cuentaDao.find(123456)).thenReturn(null);

        assertThrows(IllegalArgumentException.class, () -> cuentaService.darDeAltaCuenta(cuentaNoSoportada, 29857643));
    }

    @Test
    public void testDarDeAltaCuentaClienteYaTieneCuentaDeEseTipo() throws TipoCuentaAlreadyExistsException {
        Cuenta cuenta = new Cuenta();
        cuenta.setNumeroCuenta(123456);
        cuenta.setTipoCuenta(TipoCuenta.CAJA_AHORRO);
        cuenta.setMoneda(TipoMoneda.PESOS);

        when(cuentaDao.find(123456)).thenReturn(null);

        doThrow(new TipoCuentaAlreadyExistsException("El cliente ya posee una cuenta de ese tipo y moneda")).when(clienteService).agregarCuenta(cuenta, 29857643);

        assertThrows(TipoCuentaAlreadyExistsException.class, () -> cuentaService.darDeAltaCuenta(cuenta, 29857643));
    }

    @Test
    public void testDarDeAltaCuentaExitosamente() throws CuentaAlreadyExistsException, TipoCuentaAlreadyExistsException {
        Cuenta cuenta = new Cuenta();
        cuenta.setNumeroCuenta(123456);
        cuenta.setTipoCuenta(TipoCuenta.CAJA_AHORRO);
        cuenta.setMoneda(TipoMoneda.PESOS);

        when(cuentaDao.find(123456)).thenReturn(null);

        cuentaService.darDeAltaCuenta(cuenta, 29857643);

        verify(clienteService, times(1)).agregarCuenta(cuenta, 29857643);
        verify(cuentaDao, times(1)).save(cuenta);
    }
}
