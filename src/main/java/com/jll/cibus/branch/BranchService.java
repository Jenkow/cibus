package com.jll.cibus.branch;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BranchService
{
    @Autowired
    private BranchRepository branchRepository;

    public boolean existsByName(String name) {
        return branchRepository.existsByName(name);
    }
    public BranchEntity findByName(String name) {
        if (!existsByName(name))
        {
            throw new RuntimeException("Branch not found with name: "+ name);
        }
        return branchRepository.findByName(name).get();
    }
    public void nameVerification (String name)
    {
        if (branchRepository.existsByName(name))
        {
            throw new RuntimeException("FAILED TO REGISTER: theres another branch with the name "+ name);
        }
    }
    public boolean existsByAdress (String street, Integer number)
    {
        return branchRepository.existsByStreetAndNumber(street, number);
    }
    public BranchEntity findByStreetAndNumber (String street, Integer number)
    {
        if (!existsByAdress(street, number))
        {
            throw new RuntimeException("Branch not found with adress "+ street+" "+number);
        }
        return branchRepository.findByStreetAndNumber(street, number).get();
    }
    public void adressVerification (String street, Integer number)
    {
        if (branchRepository.existsByStreetAndNumber(street, number))
        {
            throw new RuntimeException("FAILED TO REGISTER: theres another branch in "+ street+" "+number);
        }
    }
    public BranchEntity registerBranch(BranchEntity newBranch)
    {
        nameVerification(newBranch.getName());
        adressVerification(newBranch.getStreet(), newBranch.getNumber());

        return branchRepository.save(newBranch);
    }
    public List<BranchEntity> getAllBranches ()
    {
        return branchRepository.findAll();
    }
    public void idVerification (Long id)
    {
        if (!branchRepository.existsById(id))
        {
            throw new RuntimeException("THERES NO BRANCH WITH ID "+ id);
        }
    }

    public BranchEntity updateBranch (Long id, BranchEntity updatedBranch)
    {
        idVerification(id);
        BranchEntity branchBase = branchRepository.findById(id).get();

        if (!branchBase.getName().equalsIgnoreCase(updatedBranch.getName()))
        {
            nameVerification(updatedBranch.getName());
            branchBase.setName(updatedBranch.getName());
        }
        if (!branchBase.getStreet().equals(updatedBranch.getStreet()) || !branchBase.getNumber().equals(updatedBranch.getNumber()))
        {
            adressVerification(updatedBranch.getStreet(), updatedBranch.getNumber());
            branchBase.setStreet(updatedBranch.getStreet());
            branchBase.setNumber(updatedBranch.getNumber());
        }
        return branchRepository.save(branchBase);
    }
    public void deleteBranch (Long id)
    {
        idVerification(id);
        branchRepository.deleteById(id);
    }

}
