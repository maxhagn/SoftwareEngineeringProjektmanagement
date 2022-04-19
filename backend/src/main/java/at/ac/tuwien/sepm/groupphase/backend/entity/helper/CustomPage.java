package at.ac.tuwien.sepm.groupphase.backend.entity.helper;

import org.springframework.data.domain.Page;

import java.util.List;

public class CustomPage<T> {
    private Page<T> page;

    public CustomPage(Page<T> page) {
        this.page = page;
    }

    public int getTotalPages() {
        return page.getTotalPages();
    }

    public int getNumber() {
        return page.getNumber();
    }

    public boolean hasNext() {
        return page.hasNext();
    }

    public List<T> getContent() {
        return page.getContent();
    }
}
