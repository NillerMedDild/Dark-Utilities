package net.darkhax.darkutils.features.monolith;

import net.darkhax.bookshelf.block.BlockTileEntity;
import net.darkhax.bookshelf.registry.IVariant;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class BlockMonolith extends BlockTileEntity implements IVariant {

    public static final String[] TYPES = new String[] { "exp", "spawning" };
    public static final PropertyEnum<EnumType> VARIANT = PropertyEnum.<EnumType> create("variant", EnumType.class);

    protected BlockMonolith () {

        super(Material.ROCK);
        this.setHardness(1.5f);
        this.setLightLevel(0.25f);
        this.setHarvestLevel("pickaxe", 1);
    }

    @Override
    public boolean canPlaceBlockAt (World world, BlockPos pos) {

        // Prevents placing if there is already a chunk.

        if (world instanceof WorldServer) {

            return !TileEntityMonolith.validatePosition((WorldServer) world, null, pos, false) ? false : super.canPlaceBlockAt(world, pos);
        }

        return true;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void getSubBlocks (CreativeTabs tab, NonNullList<ItemStack> list) {

        for (int meta = 0; meta < TYPES.length; meta++) {

            list.add(new ItemStack(this, 1, meta));
        }
    }

    @Override
    public IBlockState getStateFromMeta (int meta) {

        return this.getDefaultState().withProperty(VARIANT, EnumType.byMetadata(meta));
    }

    @Override
    public int getMetaFromState (IBlockState state) {

        return state.getValue(VARIANT).getMetadata();
    }

    @Override
    protected BlockStateContainer createBlockState () {

        return new BlockStateContainer(this, new IProperty[] { VARIANT });
    }

    @Override
    public TileEntity createNewTileEntity (World worldIn, int meta) {

        if (meta == 0) {

            return new TileEntityMonolithEXP();
        }

        else if (meta == 1) {

            return new TileEntityMonolithSpawning();
        }

        return null;
    }

    @Override
    public boolean isFullCube (IBlockState state) {

        return false;
    }

    @Override
    public boolean isOpaqueCube (IBlockState state) {

        return false;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public BlockRenderLayer getBlockLayer () {

        return BlockRenderLayer.CUTOUT;
    }

    public static boolean isEnabled (World world, BlockPos pos) {

        return !world.isBlockPowered(pos) && world.isAirBlock(pos.up());
    }

    @Override
    public String[] getVariant () {

        return TYPES;
    }

    public static enum EnumType implements IStringSerializable {

        EXP(0, "exp"),
        SPAWNING(1, "spawning");

        private static final EnumType[] META_LOOKUP = new EnumType[values().length];

        private final int meta;

        private final String name;

        private EnumType (int meta, String name) {

            this.meta = meta;
            this.name = name;
        }

        public int getMetadata () {

            return this.meta;
        }

        @Override
        public String toString () {

            return this.name;
        }

        public static EnumType byMetadata (int meta) {

            if (meta < 0 || meta >= META_LOOKUP.length) {
                meta = 0;
            }

            return META_LOOKUP[meta];
        }

        @Override
        public String getName () {

            return this.name;
        }

        static {

            for (final EnumType type : values()) {
                META_LOOKUP[type.getMetadata()] = type;
            }
        }
    }
}