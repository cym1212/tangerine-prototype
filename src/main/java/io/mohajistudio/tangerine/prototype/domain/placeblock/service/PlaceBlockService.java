package io.mohajistudio.tangerine.prototype.domain.placeblock.service;

import io.mohajistudio.tangerine.prototype.domain.place.domain.Place;
import io.mohajistudio.tangerine.prototype.domain.place.service.PlaceService;
import io.mohajistudio.tangerine.prototype.domain.placeblockimage.service.PlaceBlockImageService;
import io.mohajistudio.tangerine.prototype.domain.post.domain.PlaceBlock;
import io.mohajistudio.tangerine.prototype.domain.placeblock.repository.PlaceBlockRepository;
import io.mohajistudio.tangerine.prototype.domain.post.domain.Post;
import io.mohajistudio.tangerine.prototype.domain.post.repository.PlaceBlockImageRepository;
import io.mohajistudio.tangerine.prototype.global.enums.ErrorCode;
import io.mohajistudio.tangerine.prototype.global.error.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

import static io.mohajistudio.tangerine.prototype.global.enums.ErrorCode.INVALID_REPRESENTATIVE_PLACE_BLOCK_IMAGE_ORDER_NUMBER;

@Service
@RequiredArgsConstructor
public class PlaceBlockService {
    private final PlaceBlockRepository placeBlockRepository;
    private final PlaceBlockImageService placeBlockImageService;
    private final PlaceBlockImageRepository placeBlockImageRepository;
    private final PlaceService placeService;

    public void addPlaceBlock(PlaceBlock placeBlock, Post post) {
        Place place = placeService.addPlace(placeBlock.getPlace());

        placeBlock.setPost(post);
        placeBlock.setMember(post.getMember());
        placeBlock.setPlace(place);

        placeBlockRepository.save(placeBlock);
        placeBlockImageService.copyImagesToPermanent(placeBlock.getPlaceBlockImages());

        placeBlock.getPlaceBlockImages().forEach(placeBlockImage -> {
            placeBlockImage.setPlaceBlock(placeBlock);
            placeBlockImageRepository.save(placeBlockImage);
            if (placeBlock.getRepresentativePlaceBlockImageOrderNumber() == placeBlockImage.getOrderNumber()) {
                placeBlock.setRepresentativePlaceBlockImageId(placeBlockImage.getId());
                placeBlockRepository.update(placeBlock.getId(), placeBlockImage.getId());
            }
        });

        if (placeBlock.getRepresentativePlaceBlockImageId() == null) {
            throw new BusinessException(INVALID_REPRESENTATIVE_PLACE_BLOCK_IMAGE_ORDER_NUMBER);
        }

    }

    public void modifyPlaceBlock(PlaceBlock placeBlock, Post post) {
        Place place = placeService.addPlace(placeBlock.getPlace());
        placeBlock.setPlace(place);

        if (placeBlock.getId() == null) {
            placeBlock.setPost(post);
            placeBlock.setMember(post.getMember());
            placeBlockRepository.save(placeBlock);
        } else {
            Optional<PlaceBlock> findPlaceBlock = placeBlockRepository.findById(placeBlock.getId());
            if (findPlaceBlock.isEmpty()) {
                throw new BusinessException(ErrorCode.INVALID_INPUT_VALUE);
            }
            placeBlockRepository.update(findPlaceBlock.get().getId(), placeBlock.getContent(), placeBlock.getOrderNumber(), placeBlock.getRating(), placeBlock.getPlaceCategory(), placeBlock.getPlace(), placeBlock.getVisitStartDate(), placeBlock.getVisitEndDate());
        }

        placeBlockImageService.copyImagesToPermanent(placeBlock.getPlaceBlockImages());

        placeBlock.getPlaceBlockImages().forEach(placeBlockImage -> {
                    placeBlockImageService.modifyPlaceBlockImage(placeBlock, placeBlockImage);
                    if (placeBlock.getRepresentativePlaceBlockImageOrderNumber() == placeBlockImage.getOrderNumber()) {
                        placeBlock.setRepresentativePlaceBlockImageId(placeBlockImage.getId());
                        placeBlockRepository.update(placeBlock.getId(), placeBlockImage.getId());
                    }
                }
        );

        if (placeBlock.getRepresentativePlaceBlockImageId() == null) {
            throw new BusinessException(INVALID_REPRESENTATIVE_PLACE_BLOCK_IMAGE_ORDER_NUMBER);
        }

    }
}
