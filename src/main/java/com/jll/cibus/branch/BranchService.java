package com.jll.cibus.branch;

import com.jll.cibus.common.exception.BusinessException;
import com.jll.cibus.common.exception.ResourceNotFoundException;
import com.jll.cibus.branch.BranchResponseDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BranchService
{
    @Autowired
    private BranchRepository branchRepository;
    @Autowired
    private BranchMapper branchMapper;

    public List<BranchEntity> getAllBranches ()
    {
        return branchRepository.findAll();
    }

    private BranchEntity getBranchById(Long id)
    {
        return branchRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("branch", id));
    }

    private BranchEntity getBranchByName(String name)
    {
        return branchRepository.findByName(name)
                .orElseThrow(() -> new ResourceNotFoundException("branch", name));
    }
    private BranchEntity getBranchByAdress (String street, Integer number)
    {
        return branchRepository.findByStreetAndNumber(street,number)
                .orElseThrow(() ->new ResourceNotFoundException("Branchs Address", street + " " + number));
    }
    public void idVerification (Long id)
    {
        if (!branchRepository.existsById(id))
        {
            throw new RuntimeException("THERES NO BRANCH WITH ID "+ id);
        }
    }
    private void nameVerification (String name)
    {
        if (branchRepository.existsByName(name))
        {
            throw new BusinessException("FAILED TO REGISTER: theres another branch with the name "+ name);
        }
    }
    private void addressVerification (String street, Integer number)
    {
        if (branchRepository.existsByStreetAndNumber(street, number))
        {
            throw new BusinessException("FAILED TO REGISTER: theres another branch in "+ street+" "+number);
        }
    }
    public BranchResponseDTO createBranch(BranchRequestDTO requestDTO)
    {
        nameVerification(requestDTO.getName());
        addressVerification(requestDTO.getStreet(), requestDTO.getNumber());

        BranchEntity entity = branchMapper.toEntity(requestDTO);
        BranchEntity saved = branchRepository.save(entity);

        return branchMapper.toResponseDTO(saved);
    }

    public BranchResponseDTO findByStreetAndNumber (String street, Integer number)
    {
        BranchEntity branch =getBranchByAdress(street,number);
        return branchMapper.toResponseDTO(branch);
    }


    public BranchResponseDTO updateBranch (Long id, BranchRequestDTO dto)
    {
        idVerification(id);
        BranchEntity branchBase = getBranchById(id);

        if (!branchBase.getName().equalsIgnoreCase(dto.getName()))
        {
            nameVerification(dto.getName());
            branchBase.setName(dto.getName());
        }
        if (!branchBase.getStreet().equals(dto.getStreet()) || !branchBase.getNumber().equals(dto.getNumber()))
        {
            addressVerification(dto.getStreet(), dto.getNumber());
            branchBase.setStreet(dto.getStreet());
            branchBase.setNumber(dto.getNumber());
        }
        BranchEntity saved = branchRepository.save(branchBase);
        return branchMapper.toResponseDTO(saved);
    }

    public void deleteBranch (Long id)
    {
        idVerification(id);
        branchRepository.deleteById(id);
    }

}
