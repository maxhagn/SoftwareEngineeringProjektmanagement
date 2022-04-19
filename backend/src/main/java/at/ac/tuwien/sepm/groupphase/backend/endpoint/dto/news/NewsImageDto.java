package at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.news;

import at.ac.tuwien.sepm.groupphase.backend.entity.File;
import at.ac.tuwien.sepm.groupphase.backend.entity.News;

import java.util.Objects;

public class NewsImageDto {

    private String id;

    private String original_name;

    private File file;

    private News news;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getOriginal_name() {
        return original_name;
    }

    public void setOriginal_name(String original_name) {
        this.original_name = original_name;
    }

    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
    }

    public News getNews() {
        return news;
    }

    public void setNews(News news) {
        this.news = news;
    }
    public NewsImageDto(){}

    public NewsImageDto(String id, String original_name, File file, News news) {
        this.id = id;
        this.original_name = original_name;
        this.file = file;
        this.news = news;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        NewsImageDto that = (NewsImageDto) o;
        return Objects.equals(id, that.id) &&
            Objects.equals(original_name, that.original_name) &&
            Objects.equals(file, that.file) &&
            Objects.equals(news, that.news);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, original_name, file, news);
    }

    public final static class NewsImageDtoBuilder {
        private String id;
        private String original_name;
        private File file;
        private News news;

        public static NewsImageDto.NewsImageDtoBuilder builder() {
            return new NewsImageDtoBuilder();
        }

        public NewsImageDtoBuilder setId(String id) {
            this.id = id;
            return this;
        }

        public NewsImageDtoBuilder setOriginal_name(String original_name) {
            this.original_name = original_name;
            return this;
        }

        public NewsImageDtoBuilder setFile(File file) {
            this.file = file;
            return this;
        }

        public NewsImageDtoBuilder setNews(News news) {
            this.news = news;
            return this;
        }

        public NewsImageDto build() {
            NewsImageDto newsImageDto = NewsImageDtoBuilder.builder()
                .setFile(file)
                .setNews(news)
                .setOriginal_name(original_name)
                .build();

            return newsImageDto;
        }
    }
}
