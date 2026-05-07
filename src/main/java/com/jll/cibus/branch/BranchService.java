package com.jll.cibus.branch;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class BranchService
{
    @Autowired
    private BranchRepository branchRepository;

    public void nameVerification (String name)
    {
        if (branchRepository.existsByName(name))
        {
            throw new RuntimeException("FAILED TO REGISTER: theres another branch with the name "+ name);
        }
    }
    public void adressVerification (String street, Integer number)
    {
        if (branchRepository.exist)
    }
}
