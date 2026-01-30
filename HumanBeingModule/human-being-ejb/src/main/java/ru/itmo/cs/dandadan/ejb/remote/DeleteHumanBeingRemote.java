package ru.itmo.cs.dandadan.ejb.remote;

import jakarta.ejb.Local;
import ru.itmo.cs.dandadan.util.Result;

@Local
public interface DeleteHumanBeingRemote {
    Result<Void> deleteHumanBeing(Long id);
}
